package de.fr3qu3ncy.easyconfignew.core.io;

import javax.annotation.Nonnull;

public interface DataWriter {

    void writeData(@Nonnull String key, Object object);
}
