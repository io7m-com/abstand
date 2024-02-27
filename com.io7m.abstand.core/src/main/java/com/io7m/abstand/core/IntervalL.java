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
 * @param lowerL The lower bound
 * @param upperL The upper bound
 */

public record IntervalL(
  long lowerL,
  long upperL)
  implements IntervalType<Long>
{
  @Override
  public String toString()
  {
    return "[%s, %s]".formatted(this.lowerL, this.upperL);
  }

  @Override
  public Long lower()
  {
    return this.lowerL;
  }

  @Override
  public Long upper()
  {
    return this.upperL;
  }

  /**
   * An inclusive interval.
   *
   * @param lowerL The lower bound
   * @param upperL The upper bound
   */

  public IntervalL
  {
    if (upperL < lowerL) {
      throw new IllegalArgumentException(
        "Interval upper %s must be >= interval lower %s"
          .formatted(upperL, lowerL)
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

  public static IntervalL of(
    final long lower,
    final long upper)
  {
    return new IntervalL(
      lower,
      upper
    );
  }

  @Override
  public boolean overlaps(
    final IntervalType<Long> other)
  {
    return this.lowerL <= other.upper()
           && other.lower() <= this.upperL;
  }

  @Override
  public IntervalType<Long> upperMaximum(
    final IntervalType<Long> other)
  {
    return new IntervalL(
      this.lowerL,
      Long.max(this.upperL, other.upper())
    );
  }
}
