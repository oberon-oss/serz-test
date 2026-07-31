package eu.oberon.oss.tools.tsc.serz.data.tags;

public interface Attribute {
    int offset();
    String name();
    <T> T getValue();
}
