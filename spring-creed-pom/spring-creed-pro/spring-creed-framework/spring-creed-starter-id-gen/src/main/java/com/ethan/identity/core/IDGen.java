package com.ethan.identity.core;

import com.ethan.identity.core.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
