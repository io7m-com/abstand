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

package com.io7m.abstand.tests;

import com.io7m.abstand.core.IntervalD;
import com.io7m.abstand.core.IntervalTree;
import com.io7m.abstand.core.IntervalTreeDebuggableType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

import java.util.List;

/**
 * Tests for interval trees.
 */

public final class IntervalTreeDTest
  extends IntervalTreeContract<IntervalD, Double>
{
  @Override
  protected IntervalD interval(
    final long lower,
    final long upper)
  {
    return IntervalD.of(lower, upper);
  }

  @Provide
  public Arbitrary<List<IntervalD>> intervals()
  {
    return Arbitraries.defaultFor(IntervalD.class)
      .list();
  }

  @Override
  protected IntervalTreeDebuggableType<Double> create()
  {
    final var t = IntervalTree.create();
    t.enableInternalValidation(true);
    return (IntervalTreeDebuggableType<Double>) t;
  }
}
