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

import com.io7m.abstand.core.IntervalB;
import com.io7m.abstand.core.IntervalTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static com.io7m.abstand.tests.IntervalDot.dot;

public final class IntervalTreeBDemo0
{
  private static final Logger LOG =
    LoggerFactory.getLogger(IntervalTreeBDemo0.class);

  private IntervalTreeBDemo0()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var t = IntervalTree.<BigInteger>create();
    t.setChangeListener(change -> LOG.debug("Change: {}", change));
    t.enableInternalValidation(true);

    dot(t, 0);
    t.insert(IntervalB.of(0L, 1L));
    dot(t, 1);
    t.insert(IntervalB.of(0L, 0L));
    dot(t, 2);
    t.insert(IntervalB.of(-1L, 0L));
    dot(t, 3);
    t.insert(IntervalB.of(-1L, 1L));
    dot(t, 4);

    t.find(IntervalB.of(-1L, 1L));
  }
}
