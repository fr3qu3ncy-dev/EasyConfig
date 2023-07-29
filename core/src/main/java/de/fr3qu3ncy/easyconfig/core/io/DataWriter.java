package de.fr3qu3ncy.easyconfig.core.io;

import javax.annotation.Nonnull;

public interface DataWriter {

    void writeData(@Nonnull String key, Object object);
}
