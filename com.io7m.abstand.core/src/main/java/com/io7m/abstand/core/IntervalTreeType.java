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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The type of mutable interval trees. Interval trees effectively act as
 * sorted sets, although they do not implement the full sorted set interface.
 *
 * @param <S> The type of scalar values
 */

public interface IntervalTreeType<S extends Comparable<S>>
  extends Set<IntervalType<S>>
{
  /**
   * Set the change listener invoked when the tree is changed.
   *
   * @param listener The listener
   */

  void setChangeListener(IntervalTreeChangeListenerType<S> listener);

  @Override
  default boolean contains(
    final Object o)
  {
    return this.find((IntervalType<S>) o);
  }

  @Override
  default boolean add(
    final IntervalType<S> i)
  {
    return this.insert(i);
  }

  @Override
  default boolean remove(
    final Object o)
  {
    return this.remove((IntervalType<S>) o);
  }

  @Override
  default boolean containsAll(
    final Collection<?> c)
  {
    var present = true;
    for (final var x : c) {
      present = present && this.contains(x);
    }
    return present;
  }

  @Override
  default boolean addAll(
    final Collection<? extends IntervalType<S>> c)
  {
    var changed = false;
    for (final var x : c) {
      changed = changed | this.add(x);
    }
    return changed;
  }

  @Override
  default boolean removeAll(
    final Collection<?> c)
  {
    var changed = false;
    for (final var x : c) {
      changed = changed | this.remove(x);
    }
    return changed;
  }

  @Override
  default boolean retainAll(
    final Collection<?> c)
  {
    var changed = false;
    final var existing = new HashSet<>(this);
    for (final var x : existing) {
      if (!c.contains(x)) {
        changed = changed | this.remove(x);
      }
    }
    return changed;
  }

  /**
   * Insert an interval into the tree.
   *
   * @param value The interval
   *
   * @return {@code true} if the interval was not already present in the tree
   */

  boolean insert(IntervalType<S> value);

  /**
   * Remove an interval from the tree.
   *
   * @param value The interval
   *
   * @return {@code true} if the interval was present in the tree
   */

  boolean remove(IntervalType<S> value);

  /**
   * @param value The interval
   *
   * @return {@code true} if the exact interval is present in the tree
   */

  boolean find(IntervalType<S> value);

  /**
   * @param interval The interval
   *
   * @return The set of intervals that overlap {@code interval}, if any
   */

  Collection<IntervalType<S>> overlapping(IntervalType<S> interval);
}
