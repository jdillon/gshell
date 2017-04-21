package com.planet57.gshell.util.completer;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Customized strings completer which provides mutable candidates.
 *
 * @since 3.0
 */
public class StringsCompleter2
  implements Completer
{
  private final Collection<Candidate> candidates = new ArrayList<>();

  private volatile boolean initalized = false;

  public StringsCompleter2() {
    // empty
  }

  public StringsCompleter2(final String... strings) {
    setStrings(strings);
  }

  public StringsCompleter2(final Iterable<String> strings) {
    setStrings(strings);
  }

  public void setStrings(final String... strings) {
    checkNotNull(strings);
    setStrings(Arrays.asList(strings));
  }

  public void setStrings(final Iterable<String> strings) {
    checkNotNull(strings);
    for (String string : strings) {
      addString(string);
    }
  }

  public void addString(final String string) {
    checkNotNull(string);
    candidates.add(new Candidate(AttributedString.stripAnsi(string), string, null, null, null, null, true));
  }

  public void removeString(final String string) {
    checkNotNull(string);

    // FIXME: not ideal, but then again neither is the rest of this impl but its used everywhere in gshell presently
    Iterator<Candidate> iter = candidates.iterator();
    while (iter.hasNext()) {
      Candidate candidate = iter.next();
      if (string.equals(candidate.value())) {
        iter.remove();
        return;
      }
    }
  }

  protected void init() {
    // empty
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    checkNotNull(candidates);

    if (!initalized) {
      init();
      initalized = true;
    }

    candidates.addAll(this.candidates);
  }
}
