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
 * An inclusive interval.
 *
 * @param lower The lower bound
 * @param upper The upper bound
 *
 * @param <G> The type of interval components
 */

public record IntervalG<G extends Comparable<G>>(
  G lower,
  G upper)
  implements IntervalType<G>
{
  @Override
  public String toString()
  {
    return "[%s, %s]".formatted(this.lower, this.upper);
  }

  /**
   * An inclusive interval.
   *
   * @param lower The lower bound
   * @param upper The upper bound
   */

  public IntervalG
  {
    Objects.requireNonNull(lower, "lower");
    Objects.requireNonNull(upper, "upper");

    if (upper.compareTo(lower) < 0) {
      throw new IllegalArgumentException(
        "Interval upper %s must be >= interval lower %s"
          .formatted(upper, lower)
      );
    }
  }

  /**
   * Convenience method for constructing intervals.
   *
   * @param lower The lower bound
   * @param upper The upper bound
   * @param <G>   The type of interval component values
   *
   * @return The interval
   */

  public static <G extends Comparable<G>> IntervalG<G> of(
    final G lower,
    final G upper)
  {
    return new IntervalG<>(lower, upper);
  }

  @Override
  public boolean overlaps(
    final IntervalType<G> other)
  {
    return this.lower.compareTo(other.upper()) <= 0
           && other.lower().compareTo(this.upper) <= 0;
  }

  private static <T extends Comparable<T>> T max(
    final T x,
    final T y)
  {
    return x.compareTo(y) >= 0 ? x : y;
  }

  @Override
  public IntervalType<G> upperMaximum(
    final IntervalType<G> other)
  {
    return new IntervalG<>(
      this.lower,
      max(this.upper, other.upper())
    );
  }
}
