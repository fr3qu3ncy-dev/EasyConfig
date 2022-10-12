package de.fr3qu3ncy.easyconfignew.core.io;

public interface DataSource {

    <T> T getData(String key);

}
