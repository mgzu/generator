/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 表信息，关联到当前字段信息
 *
 * @author YangHu
 * @since 2016/8/30
 */
public class TableInfo {
    private final StrategyConfig strategyConfig;
    private final GlobalConfig globalConfig;
    private final Set<String> importPackages = new TreeSet<>();
    private boolean convert;
    private String name;
    private String comment;
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    private final List<TableField> fields = new ArrayList<>();
    private boolean havePrimaryKey;
    /**
     * 公共字段
     */
    private final List<TableField> commonFields = new ArrayList<>();
    private String fieldNames;

    private final Entity entity;
    /**
     * 主键
     */
    private TableField primaryKey = null;
    // TODO: 多主键
    private final List<TableField> primaryKeys = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param configBuilder 配置构建
     * @param name          表名
     * @since 3.5.0
     */
    public TableInfo(@NotNull ConfigBuilder configBuilder, @NotNull String name) {
        this.strategyConfig = configBuilder.getStrategyConfig();
        this.globalConfig = configBuilder.getGlobalConfig();
        this.entity = configBuilder.getStrategyConfig().entity();
        this.name = name;
    }

    /**
     * @since 3.5.0
     */
    protected TableInfo setConvert() {
        if (strategyConfig.startsWithTablePrefix(name) || entity.isTableFieldAnnotationEnable()) {
            // 包含前缀
            this.convert = true;
        } else if (strategyConfig.isCapitalModeNaming(name)) {
            // 包含
            this.convert = !entityName.equalsIgnoreCase(name);
        } else {
            // 转换字段
            if (NamingStrategy.underline_to_camel == entity.getColumnNaming()) {
                // 包含大写处理
                if (StringUtils.containsUpperCase(name)) {
                    this.convert = true;
                }
            } else if (!entityName.equalsIgnoreCase(name)) {
                this.convert = true;
            }
        }
        return this;
    }

    public String getEntityPath() {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

    /**
     * @param entityName 实体名称
     * @return this
     */
    public TableInfo setEntityName(@NotNull String entityName) {
        this.entityName = entityName;
        //TODO 先放置在这里
        setConvert();
        return this;
    }

    /**
     * 添加字段
     *
     * @param field 字段
     * @since 3.5.0
     */
    public void addField(@NotNull TableField field) {
        if (entity.matchIgnoreColumns(field.getColumnName())) {
            // 忽略字段不在处理
            return;
        } else if (entity.matchSuperEntityColumns(field.getColumnName())) {
            this.commonFields.add(field);
        } else {
            this.fields.add(field);
        }
    }

    /**
     * @param pkgs 包空间
     * @return this
     * @since 3.5.0
     */
    public TableInfo addImportPackages(@NotNull String... pkgs) {
        importPackages.addAll(Arrays.asList(pkgs));
        return this;
    }

    /**
     * 转换filed实体为 xml mapper 中的 base column 字符串信息
     */
    public String getFieldNames() {
        //TODO 感觉这个也啥必要,不打算公开set方法了
        if (StringUtils.isBlank(fieldNames)) {
            this.fieldNames = this.fields.stream().map(TableField::getColumnName).collect(Collectors.joining(", "));
        }
        return this.fieldNames;
    }

    /**
     * 导包处理
     *
     * @since 3.5.0
     */
    public void importPackage() {
        boolean importSerializable = true;
        String superEntity = entity.getSuperClass();
        if (StringUtils.isNotBlank(superEntity)) {
            // 自定义父类
            importSerializable = false;
            this.importPackages.add(superEntity);
        } else {
            if (entity.isActiveRecord()) {
                // 无父类开启 AR 模式
                this.importPackages.add(Model.class.getCanonicalName());
                importSerializable = false;
            }
        }
        if (importSerializable) {
            this.importPackages.add(Serializable.class.getCanonicalName());
        }
        if (this.isConvert() && this.globalConfig.isEnableMybatisPlus()) {
            this.importPackages.add(TableName.class.getCanonicalName());
        }
        IdType idType = entity.getIdType();
        if (null != idType && this.isHavePrimaryKey() && this.globalConfig.isEnableMybatisPlus()) {
            // 指定需要 IdType 场景
            this.importPackages.add(IdType.class.getCanonicalName());
            this.importPackages.add(TableId.class.getCanonicalName());
        }
        this.fields.forEach(field -> {
            IColumnType columnType = field.getColumnType();
            if (null != columnType && null != columnType.getPkg()) {
                importPackages.add(columnType.getPkg());
            }
            if (field.isKeyFlag()) {
                // 主键
                if ((field.isConvert() || field.isKeyIdentityFlag()) && this.globalConfig.isEnableMybatisPlus()) {
                    importPackages.add(TableId.class.getCanonicalName());
                }
                // 自增
                if (field.isKeyIdentityFlag() && this.globalConfig.isEnableMybatisPlus()) {
                    importPackages.add(IdType.class.getCanonicalName());
                }
            } else if (field.isConvert() && this.globalConfig.isEnableMybatisPlus()) {
                // 普通字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
            }
            if (null != field.getFill() && this.globalConfig.isEnableMybatisPlus()) {
                // 填充字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                //TODO 好像default的不用处理也行,这个做优化项目.
                importPackages.add(FieldFill.class.getCanonicalName());
            }
            if (field.isVersionField() && this.globalConfig.isEnableMybatisPlus()) {
                this.importPackages.add(Version.class.getCanonicalName());
            }
            if (field.isLogicDeleteField() && this.globalConfig.isEnableMybatisPlus()) {
                this.importPackages.add(TableLogic.class.getCanonicalName());
            }
        });
    }

    /**
     * 处理表信息(文件名与导包)
     *
     * @since 3.5.0
     */
    public void processTable() {
        String entityName = entity.getNameConvert().entityNameConvert(this);
        this.setEntityName(entity.getConverterFileName().convert(entityName));
        this.mapperName = strategyConfig.mapper().getConverterMapperFileName().convert(entityName);
        this.xmlName = strategyConfig.mapper().getConverterXmlFileName().convert(entityName);
        this.serviceName = strategyConfig.service().getConverterServiceFileName().convert(entityName);
        this.serviceImplName = strategyConfig.service().getConverterServiceImplFileName().convert(entityName);
        this.controllerName = strategyConfig.controller().getConverterFileName().convert(entityName);
        this.importPackage();
    }

    public TableInfo setComment(String comment) {
        //TODO 暂时挪动到这
        this.comment = this.globalConfig.isSwagger()
            && StringUtils.isNotBlank(comment) ? comment.replace("\"", "\\\"") : comment;
        return this;
    }

    public TableInfo setHavePrimaryKey(boolean havePrimaryKey) {
        this.havePrimaryKey = havePrimaryKey;
        return this;
    }

    @NotNull
    public Set<String> getImportPackages() {
        return importPackages;
    }

    public boolean isConvert() {
        return convert;
    }

    public TableInfo setConvert(boolean convert) {
        this.convert = convert;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getMapperName() {
        return mapperName;
    }

    public String getXmlName() {
        return xmlName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceImplName() {
        return serviceImplName;
    }

    public String getControllerName() {
        return controllerName;
    }

    @NotNull
    public List<TableField> getFields() {
        return fields;
    }

    public boolean isHavePrimaryKey() {
        return havePrimaryKey;
    }

    @NotNull
    public List<TableField> getCommonFields() {
        return commonFields;
    }

    public TableField getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(TableField primaryKey) {
        this.primaryKey = primaryKey;
    }
}
