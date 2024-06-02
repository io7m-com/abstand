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


import com.io7m.abstand.generation.IntervalArbB;
import com.io7m.abstand.generation.IntervalArbG;
import com.io7m.abstand.generation.IntervalArbD;
import com.io7m.abstand.generation.IntervalArbI;
import com.io7m.abstand.generation.IntervalArbL;
import com.io7m.abstand.generation.IntervalArbTreeChange;
import net.jqwik.api.providers.ArbitraryProvider;

/**
 * Interval trees (Generation)
 */

module com.io7m.abstand.generation
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires com.io7m.abstand.core;
  requires net.jqwik.api;

  uses ArbitraryProvider;

  exports com.io7m.abstand.generation;

  provides ArbitraryProvider with
    IntervalArbTreeChange,
    IntervalArbG,
    IntervalArbB,
    IntervalArbL,
    IntervalArbI,
    IntervalArbD;
}
