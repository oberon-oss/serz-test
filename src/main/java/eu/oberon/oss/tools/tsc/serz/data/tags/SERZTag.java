package eu.oberon.oss.tools.tsc.serz.data.tags;

public interface SERZTag {
    SERZTagTypes getType();

    int getOffset();

    int getLength();
}
