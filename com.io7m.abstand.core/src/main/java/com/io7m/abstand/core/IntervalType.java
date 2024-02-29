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

/**
 * The base type of intervals.
 *
 * @param <S> The scalar type
 */

public interface IntervalType<S extends Comparable<S>>
  extends Comparable<IntervalType<S>>
{
  /**
   * @param other The other interval
   *
   * @return {@code true} if this interval overlaps {@code other}
   */

  boolean overlaps(IntervalType<S> other);

  /**
   * @return The inclusive upper bound
   */

  S upper();

  /**
   * @return The inclusive lower bound
   */

  S lower();

  /**
   * @param other The other interval
   *
   * @return The interval with an upper bound equal to the maximum of this and the other interval's upper bounds
   */

  IntervalType<S> upperMaximum(IntervalType<S> other);

  /**
   * Compare two intervals. Analogous to {@link Comparable#compareTo(Object)}
   * but with an enum result.
   *
   * @param other The other interval
   *
   * @return The comparison result
   */

  default IntervalComparison compare(
    final IntervalType<S> other)
  {
    final var lowerC = this.lower().compareTo(other.lower());
    if (lowerC < 0) {
      return IntervalComparison.LESS_THAN;
    }
    if (lowerC == 0) {
      final var upperC = this.upper().compareTo(other.upper());
      if (upperC < 0) {
        return IntervalComparison.LESS_THAN;
      }
      if (upperC == 0) {
        return IntervalComparison.EQUAL;
      }
      return IntervalComparison.MORE_THAN;
    }
    return IntervalComparison.MORE_THAN;
  }

  @Override
  default int compareTo(
    final IntervalType<S> other)
  {
    return switch (this.compare(other)) {
      case LESS_THAN -> -1;
      case EQUAL -> 0;
      case MORE_THAN -> 1;
    };
  }
}
