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


package com.io7m.abstand.tests;

import com.io7m.abstand.core.IntervalG;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.math.BigInteger;
import java.util.Objects;

import static com.io7m.abstand.core.IntervalComparison.EQUAL;
import static com.io7m.abstand.core.IntervalComparison.LESS_THAN;
import static com.io7m.abstand.core.IntervalComparison.MORE_THAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IntervalGTest
{
  @Property
  public void testCompareReflexive(
    final @ForAll IntervalG<BigInteger> x)
  {
    assertEquals(EQUAL, x.compare(x));
  }

  @Property
  public void testCompareAntiSymmetric(
    final @ForAll IntervalG<BigInteger> x,
    final @ForAll IntervalG<BigInteger> y)
  {
    switch (x.compare(y)) {
      case LESS_THAN -> {
        assertEquals(MORE_THAN, y.compare(x));
      }
      case EQUAL -> {
        assertEquals(EQUAL, y.compare(x));
      }
      case MORE_THAN -> {
        assertEquals(LESS_THAN, y.compare(x));
      }
    }
  }

  @Property
  public void testToString(
    final @ForAll IntervalG<BigInteger> x,
    final @ForAll IntervalG<BigInteger> y)
  {
    if (Objects.equals(x, y)) {
      assertEquals(x.toString(), y.toString());
    } else {
      assertNotEquals(x.toString(), y.toString());
    }
  }

  @Property
  public void testInvalid(
    final @ForAll BigInteger x,
    final @ForAll BigInteger y)
  {
    if (x.compareTo(y) < 0) {
      assertThrows(IllegalArgumentException.class, () -> {
        new IntervalG<>(y, x);
      });
    }
  }
}
