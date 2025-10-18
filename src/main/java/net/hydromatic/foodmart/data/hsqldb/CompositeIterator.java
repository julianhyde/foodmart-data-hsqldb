/*
 * Licensed to Julian Hyde under one or more contributor license
 * agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. Julian Hyde
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hydromatic.foodmart.data.hsqldb;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterator that concatenates multiple iterables into a single iterator. */
class CompositeIterator<E> implements Iterator<E> {
  private final Iterator<Iterable<E>> iterableIterator;
  private Iterator<E> iterator;
  private E next;

  private CompositeIterator(Iterable<Iterable<E>> iterableIterator) {
    this.iterableIterator = iterableIterator.iterator();
    advance();
  }

  /** Returns an iterable that concatenates multiple iterables. */
  static <E> Iterable<E> concat(List<Iterable<E>> iterables) {
    return () -> new CompositeIterator<>(iterables);
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    E result = next;
    advance();
    return result;
  }

  private void advance() {
    next = null;

    // Try to get next value from the current iterator.
    while (next == null) {
      if (iterator != null && iterator.hasNext()) {
        next = iterator.next();
        return;
      }

      // Current iterator exhausted. Move to next iterator.
      if (!iterableIterator.hasNext()) {
        return;
      }
      iterator = iterableIterator.next().iterator();
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}

// End CompositeIterator.java
