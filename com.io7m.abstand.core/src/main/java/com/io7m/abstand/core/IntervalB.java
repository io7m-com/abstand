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

import java.math.BigInteger;
import java.util.Objects;

/**
 * An inclusive interval.
 *
 * @param lower The lower bound
 * @param upper The upper bound
 */

public record IntervalB(
  BigInteger lower,
  BigInteger upper)
  implements IntervalType<BigInteger>
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

  public IntervalB
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
   *
   * @return The interval
   */

  public static IntervalB of(
    final long lower,
    final long upper)
  {
    return new IntervalB(
      BigInteger.valueOf(lower),
      BigInteger.valueOf(upper)
    );
  }

  @Override
  public boolean overlaps(
    final IntervalType<BigInteger> other)
  {
    return this.lower.compareTo(other.upper()) <= 0
           && other.lower().compareTo(this.upper) <= 0;
  }

  @Override
  public IntervalType<BigInteger> upperMaximum(
    final IntervalType<BigInteger> other)
  {
    return new IntervalB(
      this.lower,
      this.upper.max(other.upper())
    );
  }
}
