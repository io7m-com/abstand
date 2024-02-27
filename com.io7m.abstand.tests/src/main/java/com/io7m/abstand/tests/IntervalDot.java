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

import com.io7m.abstand.core.IntervalTreeDebuggableType;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

final class IntervalDot
{
  private IntervalDot()
  {

  }

  static void dot(
    final IntervalTreeDebuggableType<BigInteger> t,
    final int i)
    throws Exception
  {
    final var dotFile =
      Path.of("/tmp/abstand/%03d.dot".formatted(i));
    final var dotPNG =
      Path.of("/tmp/abstand/%03d.png".formatted(i));

    Files.createDirectories(dotFile.getParent());

    try (var output = Files.newOutputStream(dotFile)) {
      t.debug(output);
      output.flush();
    }

    new ProcessBuilder()
      .command("dot", "-Tpng", "-o", dotPNG.toString(), dotFile.toString())
      .start()
      .waitFor();
  }
}
