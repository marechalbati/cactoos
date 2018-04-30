/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.func;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link CheckedFunc}.
 * Some of tests don't use {@code @Test(expected = ...} intentionally as such
 * tests aren't included into test coverage statistic.
 *
 * @author Roman Proshin (roman@proshin.org)
 * @version $Id$
 * @since 1.0
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class CheckedFuncTest {

    @Test
    public void runtimeExceptionIsNotWrapped() throws Exception {
        final String message = "runtime1";
        try {
            new CheckedFunc<>(
                arg -> {
                    throw new IllegalStateException(message);
                },
                IOException::new
            ).apply("arg1");
            Assert.fail("No IllegalStateException has been thrown");
        } catch (final IllegalStateException exp) {
            MatcherAssert.assertThat(
                "IllegalStateException has unexpected message",
                exp.getMessage(),
                new IsEqual<>(message)
            );
        }
    }

    @Test
    public void checkedExceptionIsWrapped() {
        try {
            new CheckedFunc<>(
                arg -> {
                    throw new InterruptedException("runtime2");
                },
                IOException::new
            ).apply("arg2");
            Assert.fail("No IOException has been thrown");
        } catch (final IOException exp) {
            MatcherAssert.assertThat(
                "IOException has unexpected message",
                exp.getMessage(),
                new IsEqual<>("java.lang.InterruptedException: runtime2")
            );
        }
    }

    @Test
    public void extraWrappingIgnored() {
        try {
            new CheckedFunc<>(
                arg -> {
                    throw new IOException("runtime3");
                },
                IOException::new
            ).apply("arg3");
        } catch (final IOException exp) {
            MatcherAssert.assertThat(
                "Extra wrapping of IOException has been added",
                exp.getCause(),
                new IsNull<>()
            );
        }
    }
}
