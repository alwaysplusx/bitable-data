package com.harmony.bitable.dsl;

import com.google.common.base.Function;
import com.harmony.bitable.Author;
import com.harmony.bitable.core.NameFunction;
import com.harmony.bitable.core.NameInformation;


public class UpdateBuilderTest {

    public static void main(String[] args) {
        Function<Author, String> getName = Author::getName;
        for (int i = 0; i < 100; i++) {
            NameFunction<Author, String> authorName = Author::getName;
            NameInformation name = NameInformation.of(authorName);
            System.out.println(name);
        }
    }

}