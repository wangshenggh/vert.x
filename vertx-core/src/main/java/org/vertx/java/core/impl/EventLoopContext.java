/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.java.core.impl;

import io.netty.channel.EventLoop;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class EventLoopContext extends DefaultContext {

  private static final Logger log = LoggerFactory.getLogger(EventLoopContext.class);

  private int currTimingSequence;
  private boolean timing;

  public EventLoopContext(VertxInternal vertx, Executor bgExec) {
    super(vertx, bgExec);
  }

  public void execute(Runnable task) {
    getEventLoop().execute(wrapTask(task));
  }

  public boolean isOnCorrectWorker(EventLoop worker) {
    return getEventLoop() == worker;
  }

  public void startExecute() {
    int timingSequence = vertx.getTimingSequence();
    if (timingSequence > currTimingSequence) {
      // Let's time the event loop
      currTimingSequence = timingSequence;
      timing = true;
      vertx.registerEventLoopStart(this);
    }
  }

  public void endExecute() {
    if (timing) {
      vertx.registerEventLoopEnd(this);
    }
  }
}
