package com.akoo.orm;

import java.util.Map;

public interface ChainLoadFinisher {

    void finishLoad(Map<String, Object> data);
}
