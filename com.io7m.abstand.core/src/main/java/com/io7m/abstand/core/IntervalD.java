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
 * @param lowerD The lower bound
 * @param upperD The upper bound
 */

public record IntervalD(
  double lowerD,
  double upperD)
  implements IntervalType<Double>
{
  @Override
  public String toString()
  {
    return "[%s, %s]".formatted(this.lowerD, this.upperD);
  }

  @Override
  public Double lower()
  {
    return this.lowerD;
  }

  @Override
  public Double upper()
  {
    return this.upperD;
  }

  /**
   * An inclusive interval.
   *
   * @param lowerD The lower bound
   * @param upperD The upper bound
   */

  public IntervalD
  {
    if (upperD < lowerD) {
      throw new IllegalArgumentException(
        "Interval upper %s must be >= interval lower %s"
          .formatted(upperD, lowerD)
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

  public static IntervalD of(
    final double lower,
    final double upper)
  {
    return new IntervalD(
      lower,
      upper
    );
  }

  @Override
  public boolean overlaps(
    final IntervalType<Double> other)
  {
    return this.lowerD <= other.upper()
           && other.lower() <= this.upperD;
  }

  @Override
  public IntervalType<Double> upperMaximum(
    final IntervalType<Double> other)
  {
    return new IntervalD(
      this.lowerD,
      Double.max(this.upperD, other.upper())
    );
  }
}
