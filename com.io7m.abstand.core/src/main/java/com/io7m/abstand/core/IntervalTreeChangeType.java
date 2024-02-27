/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import java.util.Objects;

/**
 * A change event that has occurred to an interval tree.
 *
 * @param <S> The type of scalar values
 */

public sealed interface IntervalTreeChangeType<S extends Comparable<S>>
{
  /**
   * The tree was rebalanced.
   *
   * @param type     The type of rebalance operation
   * @param interval The interval in the rebalanced node
   * @param <S>      The type of scalar values
   */

  record Balanced<S extends Comparable<S>>(
    String type,
    IntervalType<S> interval)
    implements IntervalTreeChangeType<S>
  {
    /**
     * The tree was rebalanced.
     */

    public Balanced
    {
      Objects.requireNonNull(type, "type");
      Objects.requireNonNull(interval, "interval");
    }

    @Override
    public String toString()
    {
      return "[Balanced %s %s]".formatted(this.type, this.interval);
    }
  }

  /**
   * A new node was created in the tree.
   *
   * @param interval The interval added
   * @param <S>      The type of scalar values
   */

  record Created<S extends Comparable<S>>(
    IntervalType<S> interval)
    implements IntervalTreeChangeType<S>
  {
    /**
     * A new node was created in the tree.
     */

    public Created
    {
      Objects.requireNonNull(interval, "interval");
    }

    @Override
    public String toString()
    {
      return "[Created %s]".formatted(this.interval);
    }
  }

  /**
   * A node was deleted from the tree.
   *
   * @param type     The type of deletion operation
   * @param interval The interval in the deleted node
   * @param <S>      The type of scalar values
   */

  record Deleted<S extends Comparable<S>>(
    String type,
    IntervalType<S> interval)
    implements IntervalTreeChangeType<S>
  {
    /**
     * A node was deleted from the tree.
     */

    public Deleted
    {
      Objects.requireNonNull(type, "type");
      Objects.requireNonNull(interval, "interval");
    }

    @Override
    public String toString()
    {
      return "[Deleted %s %s]".formatted(this.type, this.interval);
    }
  }

  /**
   * All nodes were deleted from the tree.
   *
   * @param <S> The type of scalar values
   */

  record Cleared<S extends Comparable<S>>()
    implements IntervalTreeChangeType<S>
  {
    @Override
    public String toString()
    {
      return "[Cleared]";
    }
  }
}
