/*
 * Copyright (c) 2015 Rafael Baquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baqsoft.listas.ui.click_processor;

public class ChecklistItemClickData {
    public long itemId;
    public int eventType;

    // Event type constants.
    public static final int typeToggle = 0;
    public static final int typeEdit = 1;

    public ChecklistItemClickData(long itemId, int eventType) {
        this.itemId = itemId;
        this.eventType = eventType;
    }
}
