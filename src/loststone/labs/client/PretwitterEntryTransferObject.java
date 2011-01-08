/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package loststone.labs.client;

import java.io.Serializable;

/**
 * Used to transfer guestbook entry data from client-to-server through GWT RPC.
 */
public class PretwitterEntryTransferObject implements Serializable {

  /* the guestbook entry name and message */
  private String message;

  /**
   * The default constructor must be declared with public visibility so that
   * the type can be transferred through GWT-RPC.
   */
  public PretwitterEntryTransferObject() {
  }

  /**
   * Creates a guest entry transfer object specifying its name and message.
   *  
   * @param name
   * @param message
   */
  public PretwitterEntryTransferObject(String message) {
    this.message = message;
  }

  /**
   * Gets the guest entry message.
   *
   * @return the guest entry message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the guest entry message.
   *
   * @param message the new message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}

