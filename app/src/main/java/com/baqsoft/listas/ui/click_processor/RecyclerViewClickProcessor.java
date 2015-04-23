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

public class RecyclerViewClickProcessor {
    public static class ClickParams {
        public int layoutPosition;
        public Object clickData;

        public ClickParams(int layoutPosition, Object clickData) {
            this.layoutPosition = layoutPosition;
            this.clickData = clickData;
        }
    }

    public interface ClickProcessor {
        void processClick (ClickParams clickParams);
    }
}
