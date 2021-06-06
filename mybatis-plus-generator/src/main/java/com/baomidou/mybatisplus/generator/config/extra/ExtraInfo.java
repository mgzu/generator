package com.baomidou.mybatisplus.generator.config.extra;

/**
 * 扩展信息
 *
 * @author mgzu
 * @since 2021-06-06
 */
public class ExtraInfo {

    private String templatePath;
    private String outputPath;

    public ExtraInfo(String templatePath, String outputPath) {
        this.templatePath = templatePath;
        this.outputPath = outputPath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
