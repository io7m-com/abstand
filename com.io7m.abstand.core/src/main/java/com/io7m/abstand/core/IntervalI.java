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
 * An inclusive interval.
 *
 * @param lowerI The lower bound
 * @param upperI The upper bound
 */

public record IntervalI(
  int lowerI,
  int upperI)
  implements IntervalType<Integer>
{
  @Override
  public String toString()
  {
    return "[%s, %s]".formatted(this.lowerI, this.upperI);
  }

  @Override
  public Integer lower()
  {
    return this.lowerI;
  }

  @Override
  public Integer upper()
  {
    return this.upperI;
  }

  /**
   * An inclusive interval.
   *
   * @param lowerI The lower bound
   * @param upperI The upper bound
   */

  public IntervalI
  {
    if (upperI < lowerI) {
      throw new IllegalArgumentException(
        "Interval upper %s must be >= interval lower %s"
          .formatted(upperI, lowerI)
      );
    }
  }

  /**
   * Convenience method for constructing intervals.
   *
   * @param lower The lower bound
   * @param upper The upper bound
   *
   * @return The interval
   */

  public static IntervalI of(
    final int lower,
    final int upper)
  {
    return new IntervalI(
      lower,
      upper
    );
  }

  @Override
  public boolean overlaps(
    final IntervalType<Integer> other)
  {
    return this.lowerI <= other.upper()
           && other.lower() <= this.upperI;
  }

  @Override
  public IntervalType<Integer> upperMaximum(
    final IntervalType<Integer> other)
  {
    return new IntervalI(
      this.lowerI,
      Integer.max(this.upperI, other.upper())
    );
  }
}
