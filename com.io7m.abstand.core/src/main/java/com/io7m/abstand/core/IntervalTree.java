/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com\> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.abstand.core;

import com.io7m.abstand.core.IntervalTreeChangeType.Balanced;
import com.io7m.abstand.core.IntervalTreeChangeType.Cleared;
import com.io7m.abstand.core.IntervalTreeChangeType.Created;
import com.io7m.abstand.core.IntervalTreeChangeType.Deleted;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Postconditions;
import com.io7m.jaffirm.core.Preconditions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Math.max;

/**
 * An interval tree. The tree is an AVL tree storing intervals and the maximum
 * upper bounds that contain their subtrees.
 *
 * @param <S> The type of scalar values in intervals
 */

public final class IntervalTree<S extends Comparable<S>>
  implements IntervalTreeDebuggableType<S>
{
  private Node<S> root;
  private boolean validation;
  private IntervalTreeChangeListenerType<S> listener;

  /**
   * Construct an empty tree.
   */

  private IntervalTree()
  {
    this.root = null;
    this.listener = change -> {

    };
  }

  /**
   * Construct an empty tree.
   *
   * @param <S> The type of scalar values in intervals
   *
   * @return An empty tree
   */

  public static <S extends Comparable<S>> IntervalTreeDebuggableType<S> create()
  {
    return new IntervalTree<>();
  }

  @Override
  public int size()
  {
    return this.sizeTraverse(this.root);
  }

  @Override
  public boolean isEmpty()
  {
    return this.root == null;
  }

  private int sizeTraverse(
    final Node<S> node)
  {
    if (node == null) {
      return 0;
    }

    final var data = (node.interval != null) ? 1 : 0;
    return data + this.sizeTraverse(node.left) + this.sizeTraverse(node.right);
  }

  @Override
  public Collection<IntervalType<S>> overlapping(
    final IntervalType<S> interval)
  {
    if (this.root == null) {
      return List.of();
    }

    return this.overlappingAt(this.root, interval).toList();
  }

  private Stream<Node<S>> all(
    final Node<S> current)
  {
    if (current == null) {
      return Stream.empty();
    }

    final Stream<Node<S>> currentStream =
      Stream.of(current);
    final Stream<Node<S>> leftStream =
      this.all(current.left);
    final Stream<Node<S>> rightStream =
      this.all(current.right);

    return Stream.of(leftStream, currentStream, rightStream)
      .flatMap(Function.identity());
  }

  private Stream<IntervalType<S>> overlappingAt(
    final Node<S> current,
    final IntervalType<S> interval)
  {
    /*
     * Test the current node's interval against the requested interval. The
     * current node's interval is only returned if it overlaps the requested
     * interval. Note that we don't check against the maximum: The maximum is
     * used to determine if recursion should proceed into child nodes.
     */

    final Stream<IntervalType<S>> currentStream;
    if (interval.overlaps(current.interval)) {
      currentStream = Stream.of(current.interval);
    } else {
      currentStream = Stream.empty();
    }

    final Stream<IntervalType<S>> leftStream;
    if (current.left != null && current.left.maximum.overlaps(interval)) {
      leftStream = this.overlappingAt(current.left, interval);
    } else {
      leftStream = Stream.empty();
    }

    final Stream<IntervalType<S>> rightStream;
    if (current.right != null && current.right.maximum.overlaps(interval)) {
      rightStream = this.overlappingAt(current.right, interval);
    } else {
      rightStream = Stream.empty();
    }

    return Stream.of(leftStream, currentStream, rightStream)
      .flatMap(Function.identity());
  }

  private void validate()
  {
    this.validateAt(this.root);
  }

  private void validateAt(
    final Node<S> current)
  {
    if (current == null) {
      return;
    }

    current.checkInvariants();

    Invariants.checkInvariantV(
      current.balanceFactor().isBalanced(),
      "Balance factor of node %s is %s",
      current,
      current.balanceFactor()
    );

    this.validateAt(current.left);
    this.validateAt(current.right);
  }

  @Override
  public void setChangeListener(
    final IntervalTreeChangeListenerType<S> newListener)
  {
    this.listener = Objects.requireNonNull(newListener, "newListener");
  }

  @Override
  public boolean insert(
    final IntervalType<S> value)
  {
    Objects.requireNonNull(value, "value");

    try {
      this.root = this.create(null, this.root, value);
    } catch (final DuplicateIntervalException e) {
      return false;
    }

    if (this.validation) {
      this.validate();
    }
    return true;
  }

  private Node<S> create(
    final Node<S> parent,
    final Node<S> current,
    final IntervalType<S> interval)
    throws DuplicateIntervalException
  {
    /*
     * If the current node is null, we're creating a leaf of some kind.
     */

    if (current == null) {
      this.publish(new Created<>(interval));
      final var newNode = new Node<>(interval);
      newNode.setParent(parent);
      newNode.updateMaximum();
      return newNode;
    }

    switch (interval.compare(current.interval)) {
      /*
       * Otherwise, there may be a node that already exists, and we can fail
       * fast.
       */

      case EQUAL -> {
        throw new DuplicateIntervalException();
      }

      /*
       * Otherwise, the new node must be created in the left branch...
       */

      case LESS_THAN -> {
        current.takeOwnershipLeft(
          this.create(current, current.left, interval)
        );
      }

      /*
       * Or the new node must be created in the right branch...
       */

      case MORE_THAN -> {
        current.takeOwnershipRight(
          this.create(current, current.right, interval)
        );
      }
    }

    current.updateMaximum();
    current.updateHeight();
    return this.balance(current);
  }

  private Node<S> balance(
    final Node<S> current)
  {
    return switch (current.balanceFactor()) {

      /*
       * The tree is already balanced at this point.
       */

      case BALANCED, BALANCED_LEANING_LEFT, BALANCED_LEANING_RIGHT -> {
        yield current;
      }

      /*
       * If tree is left-heavy...
       */

      case LEFT_HEAVY -> {
        yield switch (current.left.balanceFactor()) {

          /*
           * If the tree's left subtree is right-heavy (or leaning right)...
           */

          case RIGHT_HEAVY, BALANCED_LEANING_RIGHT -> {
            this.publish(new Balanced<>("RL", current.interval));
            yield this.rotateRL(current);
          }
          case LEFT_HEAVY, BALANCED, BALANCED_LEANING_LEFT -> {
            this.publish(new Balanced<>("RR", current.interval));
            yield this.rotateRR(current);
          }
        };
      }

      /*
       * If tree is right-heavy...
       */

      case RIGHT_HEAVY -> {
        yield switch (current.right.balanceFactor()) {

          /*
           * If the tree's right subtree is left-heavy (or leaning left)...
           */

          case LEFT_HEAVY, BALANCED_LEANING_LEFT -> {
            this.publish(new Balanced<>("LR", current.interval));
            yield this.rotateLR(current);
          }

          case RIGHT_HEAVY, BALANCED, BALANCED_LEANING_RIGHT -> {
            this.publish(new Balanced<>("LL", current.interval));
            yield this.rotateLL(current);
          }
        };
      }
    };
  }

  /**
   * Perform an RR ("Single Right") rotation.
   *
   * @param c The current node
   *
   * @return The new root node of the subtree
   */

  private Node<S> rotateRR(
    final Node<S> c)
  {
    // B becomes the new root of the subtree.
    final var b = c.left;
    final var oldParent = c.parent;

    // C takes ownership of B's right child as its own left child.
    c.takeOwnershipLeft(b.right);

    // B takes ownership of C as its right child.
    b.takeOwnershipRight(c);

    // B's parent is now what C's _used_ to be.
    b.setParent(oldParent);

    c.updateHeight();
    b.updateHeight();
    return b;
  }

  /**
   * Perform an LL ("Single Left") rotation.
   *
   * @param a The current node
   *
   * @return The new root node of the subtree
   */

  private Node<S> rotateLL(
    final Node<S> a)
  {
    // B becomes the new root of the subtree.
    final var b = a.right;
    final var oldParent = a.parent;

    // A takes ownership of B's left child as its own right child.
    a.takeOwnershipRight(b.left);

    // B takes ownership of A as its own left child.
    b.takeOwnershipLeft(a);

    // B's parent is now what A's _used_ to be.
    b.setParent(oldParent);

    a.updateHeight();
    b.updateHeight();
    return b;
  }

  /**
   * Perform an RL ("Double Right") rotation. Yes, this naming scheme is
   * utterly misleading.
   *
   * @param current The current node
   *
   * @return The new root node of the subtree
   */

  private Node<S> rotateRL(
    final Node<S> current)
  {
    current.takeOwnershipLeft(this.rotateLL(current.left));

    Postconditions.checkPostcondition(
      current,
      current.balanceFactor() == BalanceFactor.LEFT_HEAVY,
      c -> "Node must be LEFT_HEAVY after rotation."
    );

    return this.rotateRR(current);
  }

  /**
   * Perform an LR ("Double Left") rotation. Yes, this naming scheme is
   * utterly misleading.
   *
   * @param current The current node
   *
   * @return The new root node of the subtree
   */

  private Node<S> rotateLR(
    final Node<S> current)
  {
    current.takeOwnershipRight(this.rotateRR(current.right));

    Postconditions.checkPostcondition(
      current,
      current.balanceFactor() == BalanceFactor.RIGHT_HEAVY,
      c -> "Node must be RIGHT_HEAVY after rotation."
    );

    return this.rotateLL(current);
  }

  @Override
  public boolean remove(
    final IntervalType<S> value)
  {
    Objects.requireNonNull(value, "value");

    try {
      this.root = this.removeAt(this.root, value);
    } catch (final NonexistentIntervalException e) {
      return false;
    }

    if (this.validation) {
      this.validate();
    }

    return true;
  }

  @Override
  public boolean find(
    final IntervalType<S> value)
  {
    Objects.requireNonNull(value, "value");
    return this.findAt(this.root, value);
  }

  private boolean findAt(
    final Node<S> current,
    final IntervalType<S> interval)
  {
    if (current == null) {
      return false;
    }

    return switch (interval.compare(current.interval)) {
      case EQUAL -> {
        yield true;
      }
      case LESS_THAN -> {
        yield this.findAt(current.left, interval);
      }
      case MORE_THAN -> {
        yield this.findAt(current.right, interval);
      }
    };
  }

  private Node<S> findMinimum(
    final Node<S> current)
  {
    Objects.requireNonNull(current, "current");

    if (current.left != null) {
      return this.findMinimum(current.left);
    }

    return current;
  }

  private Node<S> removeAt(
    final Node<S> current,
    final IntervalType<S> interval)
    throws NonexistentIntervalException
  {
    if (current == null) {
      throw new NonexistentIntervalException();
    }

    return switch (interval.compare(current.interval)) {
      case EQUAL -> {

        /*
         * If the current node has no children, then it is replaced with
         * nothing.
         */

        if (current.left == null && current.right == null) {
          this.publish(new Deleted<>("Leaf", interval));
          yield null;
        }

        /*
         * If the current node has only a single child, then the node is
         * replaced by its own child.
         */

        if (current.left != null ^ current.right != null) {
          if (current.left != null) {
            this.publish(new Deleted<>("SingleParentL", interval));
            yield current.left;
          } else {
            this.publish(new Deleted<>("SingleParentR", interval));
            yield current.right;
          }
        }

        /*
         * The current node must have two children. The current node is
         * effectively replaced by the successor (the node with the smallest
         * value greater than the current node). This is handled by simply
         * setting the value of the current node to that of the successor,
         * and then removing the original successor from the right subtree.
         */

        final var successor = this.findMinimum(current.right);
        current.setInterval(successor.interval);
        current.takeOwnershipRight(
          this.removeAt(current.right, successor.interval)
        );

        current.updateMaximum();
        current.updateHeight();

        this.publish(new Deleted<>("Branch", interval));
        yield this.balance(current);
      }

      case LESS_THAN -> {
        current.takeOwnershipLeft(this.removeAt(current.left, interval));
        current.updateMaximum();
        current.updateHeight();
        yield this.balance(current);
      }

      case MORE_THAN -> {
        current.takeOwnershipRight(this.removeAt(current.right, interval));
        current.updateMaximum();
        current.updateHeight();
        yield this.balance(current);
      }
    };
  }

  @Override
  public void enableInternalValidation(
    final boolean enabled)
  {
    this.validation = enabled;
  }

  @Override
  public void debug(
    final OutputStream stream)
    throws IOException
  {
    try (var writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
      try (var b = new BufferedWriter(writer)) {
        b.append("digraph G {");
        b.newLine();
        b.append("splines = polyline;");
        b.newLine();

        this.debugNodeNames(b, this.root);
        this.debugNodeStructure(b, this.root);

        b.append("}");
        b.newLine();
      }
    }
  }

  private void debugNodeStructure(
    final BufferedWriter b,
    final Node<S> c)
    throws IOException
  {
    if (c == null) {
      return;
    }

    if (c.left != null) {
      b.write("\"%s\":f3 -> \"%s\";".formatted(c, c.left));
      b.newLine();
    }
    if (c.right != null) {
      b.write("\"%s\":f4 -> \"%s\";".formatted(c, c.right));
      b.newLine();
    }

    if (c.parent != null) {
      b.write("\"%s\":f0 -> \"%s\":f0 [color=red];".formatted(c, c.parent));
      b.newLine();
    }

    this.debugNodeStructure(b, c.left);
    this.debugNodeStructure(b, c.right);
  }

  private void debugNodeNames(
    final BufferedWriter b,
    final Node<S> c)
    throws IOException
  {
    if (c == null) {
      return;
    }

    b.write("\"");
    b.write(c.toString());
    b.write("\" [");
    b.newLine();
    b.write("label = \"<f0> %s | <f1> %s | <f2> %s | <f3> L | <f4> R\"".formatted(
      c,
      c.interval,
      c.maximum));
    b.newLine();
    b.write("fontname = Monospace");
    b.newLine();
    b.write("fontsize = 12");
    b.newLine();
    b.write("shape = \"record\"");
    b.newLine();
    b.write("];");
    b.newLine();

    this.debugNodeNames(b, c.left);
    this.debugNodeNames(b, c.right);
  }

  private void publish(
    final IntervalTreeChangeType<S> change)
  {
    try {
      this.listener.onChange(Objects.requireNonNull(change, "change"));
    } catch (final Throwable e) {
      // Nothing we can do about it.
    }
  }

  @Override
  public Iterator<IntervalType<S>> iterator()
  {
    final var stream = this.all(this.root);
    return stream.map(node -> node.interval).iterator();
  }

  @Override
  public Object[] toArray()
  {
    final var elements =
      this.all(this.root)
        .map(x -> x.interval)
        .toList();

    final var output = new Object[elements.size()];
    for (int index = 0; index < output.length; ++index) {
      output[index] = elements.get(index);
    }
    return output;
  }

  @SuppressWarnings("unchecked")
  private <T> T[] prepareArray(
    final T[] a)
  {
    final int size = this.size();
    if (a.length < size) {
      return (T[]) Array.newInstance(a.getClass().getComponentType(), size);
    }
    if (a.length > size) {
      a[size] = null;
    }
    return a;
  }

  @Override
  public <T> T[] toArray(
    final T[] a)
  {
    final var elements =
      this.all(this.root)
        .map(x -> x.interval)
        .toList();

    final var output = this.prepareArray(a);
    for (int index = 0; index < elements.size(); ++index) {
      output[index] = (T) elements.get(index);
    }
    return output;
  }

  @Override
  public void clear()
  {
    this.publish(new Cleared<>());
    this.root = null;
  }

  enum BalanceFactor
  {
    /*
     * The node is completely balanced; both subtrees have the same height.
     */

    BALANCED,

    /*
     * The node is below the threshold that requires balancing, but the left
     * subtree has a greater height than the right.
     */

    BALANCED_LEANING_LEFT,

    /*
     * The node is below the threshold that requires balancing, but the right
     * subtree has a greater height than the left.
     */

    BALANCED_LEANING_RIGHT,

    /*
     * The node is unbalanced and needs balancing. The left subtree has a
     * greater height than the right.
     */

    LEFT_HEAVY,

    /*
     * The node is unbalanced and needs balancing. The right subtree has a
     * greater height than the left.
     */

    RIGHT_HEAVY;

    /**
     * @return {@code true} if this status indicates a balanced node
     */

    boolean isBalanced()
    {
      return switch (this) {
        case BALANCED, BALANCED_LEANING_LEFT, BALANCED_LEANING_RIGHT -> true;
        case LEFT_HEAVY, RIGHT_HEAVY -> false;
      };
    }
  }

  private static final class Node<S extends Comparable<S>>
  {
    private IntervalType<S> interval;
    private Node<S> left;
    private Node<S> parent;
    private Node<S> right;
    private IntervalType<S> maximum;
    private int height;

    Node(
      final IntervalType<S> inInterval)
    {
      this.interval = Objects.requireNonNull(inInterval, "interval");
      this.maximum = inInterval;
    }

    @Override
    public String toString()
    {
      return "[Node %S %s %s(%s) (H %s)]"
        .formatted(
          Integer.toUnsignedString(this.hashCode(), 16),
          this.parent == null ? "(root)" : "",
          this.balanceFactor(),
          Integer.valueOf(this.balanceFactorRaw()),
          Integer.valueOf(this.height)
        );
    }

    IntervalType<S> updateMaximum()
    {
      var newMaximum = this.maximum;
      if (this.left != null) {
        newMaximum = this.maximum.upperMaximum(this.left.updateMaximum());
      }
      if (this.right != null) {
        newMaximum = this.maximum.upperMaximum(this.right.updateMaximum());
      }
      this.maximum = newMaximum;
      return newMaximum;
    }

    int leftHeight()
    {
      if (this.left == null) {
        return 0;
      }
      return this.left.height;
    }

    int rightHeight()
    {
      if (this.right == null) {
        return 0;
      }
      return this.right.height;
    }

    /**
     * <p>Determine the balance factor of the given node.</p>
     *
     * @return A balance factor
     */

    BalanceFactor balanceFactor()
    {
      final var delta = this.balanceFactorRaw();
      if (delta > 1) {
        return BalanceFactor.LEFT_HEAVY;
      }
      if (delta > 0) {
        return BalanceFactor.BALANCED_LEANING_LEFT;
      }

      if (delta < -1) {
        return BalanceFactor.RIGHT_HEAVY;
      }
      if (delta < 0) {
        return BalanceFactor.BALANCED_LEANING_RIGHT;
      }

      return BalanceFactor.BALANCED;
    }

    int balanceFactorRaw()
    {
      final int heightL = this.leftHeight();
      final int heightR = this.rightHeight();
      return heightL - heightR;
    }

    void updateHeight()
    {
      this.height = max(this.leftHeight(), this.rightHeight()) + 1;
    }

    public void takeOwnershipLeft(
      final Node<S> node)
    {
      this.left = node;
      if (node != null) {
        node.setParent(this);
      }
      this.checkInvariants();
    }

    private void checkInvariants()
    {
      Invariants.checkInvariant(
        !Objects.equals(this, this.left),
        "A node must not be equal to its own left child."
      );
      Invariants.checkInvariant(
        !Objects.equals(this, this.right),
        "A node must not be equal to its own right child."
      );
      Invariants.checkInvariant(
        !Objects.equals(this, this.parent),
        "A node must not be equal to its own parent."
      );

      if (this.left != null) {
        final var cmp = this.left.interval.compare(this.interval);
        Invariants.checkInvariantV(
          cmp == IntervalComparison.LESS_THAN,
          "Left value node %s must be < current node value %s but is %s",
          this.left.interval,
          this.interval,
          cmp
        );
      }

      if (this.right != null) {
        final var cmp = this.right.interval.compare(this.interval);
        Invariants.checkInvariantV(
          cmp == IntervalComparison.MORE_THAN,
          "Right value node %s must be > current node value %s but is %s",
          this.right.interval,
          this.interval,
          cmp
        );
      }
    }

    private void setParent(
      final Node<S> node)
    {
      Preconditions.checkPrecondition(
        !Objects.equals(node, this),
        "A node's parent must not be equal to the node."
      );

      if (node != null) {
        Preconditions.checkPrecondition(
          !Objects.equals(node, this.left),
          "A node's parent must not be equal to its own left child."
        );
        Preconditions.checkPrecondition(
          !Objects.equals(node, this.right),
          "A node's parent must not be equal to its own right child."
        );
      }

      this.parent = node;
      this.checkInvariants();
    }

    public void takeOwnershipRight(
      final Node<S> node)
    {
      this.right = node;
      if (node != null) {
        node.setParent(this);
      }
      this.checkInvariants();
    }

    public void setInterval(
      final IntervalType<S> newInterval)
    {
      this.interval =
        Objects.requireNonNull(newInterval, "newInterval");
    }
  }

  private static final class DuplicateIntervalException
    extends Exception
  {
    DuplicateIntervalException()
    {

    }
  }

  private static final class NonexistentIntervalException
    extends Exception
  {
    NonexistentIntervalException()
    {

    }
  }
}
