package com.disney.xband.common.lib;

public class Sequence {
    private String name;
    private Long timeout = null;
    private Integer ratio = null;

    public Sequence(String name, Long timeout, Integer ratio)
    {
        this.name = name;

        if ( timeout != null )
            this.timeout = timeout;
        if ( ratio != null )
            this.ratio = ratio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }
}
