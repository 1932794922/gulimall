<template>
    <el-dialog
        :visible.sync="visible"
        :title="!dataForm.attrId ? $t('add') : $t('update')"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        @close="dialogClose"
    >
        <el-form
            :model="dataForm"
            :rules="dataRule"
            ref="dataForm"
            @keyup.enter.native="dataFormSubmitHandle()"
            :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'"
        >
            <!--       @keyup.enter.native="dataFormSubmit()" -->
            <el-form-item label="属性名" prop="attrName">
                <el-input
                    v-model="dataForm.attrName"
                    placeholder="属性名"
                ></el-input>
            </el-form-item>
            <el-form-item label="属性类型" prop="attrType">
                <el-select v-model="dataForm.attrType" placeholder="请选择">
                    <el-option label="规格参数" :value="1"></el-option>
                    <el-option label="销售属性" :value="0"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="值类型" prop="valueType">
                <el-switch
                    v-model="dataForm.valueType"
                    active-text="允许多个值"
                    inactive-text="只能单个值"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="0"
                    :active-value="1"
                ></el-switch>
            </el-form-item>
            <el-form-item label="可选值" prop="valueSelect">
                <!-- <el-input v-model="dataForm.valueSelect"></el-input> -->
                <el-select
                    v-model="dataForm.valueSelect"
                    multiple
                    filterable
                    allow-create
                    placeholder="请输入内容"
                ></el-select>
            </el-form-item>
            <el-form-item label="属性图标" prop="icon">
                <el-input
                    v-model="dataForm.icon"
                    placeholder="属性图标"
                ></el-input>
            </el-form-item>
            <el-form-item label="所属分类" prop="catelogId">
                <category-cascader
                    :catelogPath.sync="catelogPath"
                ></category-cascader>
            </el-form-item>
            <el-form-item label="所属分组" prop="attrGroupId" v-if="type == 1">
                <el-select
                    ref="groupSelect"
                    v-model="dataForm.attrGroupId"
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in attrGroups"
                        :key="item.attrGroupId"
                        :label="item.attrGroupName"
                        :value="item.attrGroupId"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="可检索" prop="searchType" v-if="type == 1">
                <el-switch
                    v-model="dataForm.searchType"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :active-value="1"
                    :inactive-value="0"
                ></el-switch>
            </el-form-item>
            <el-form-item label="快速展示" prop="showDesc" v-if="type == 1">
                <el-switch
                    v-model="dataForm.showDesc"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :active-value="1"
                    :inactive-value="0"
                ></el-switch>
            </el-form-item>
            <el-form-item label="启用状态" prop="enable">
                <el-switch
                    v-model="dataForm.enable"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :active-value="1"
                    :inactive-value="0"
                ></el-switch>
            </el-form-item>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{
                $t('confirm')
            }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import debounce from 'lodash/debounce'
import CategoryCascader from '../common/CategoryCascader'
import { attrGroupUrl } from '@/api/product/index'
export default {
    components: {
        CategoryCascader
    },
    data() {
        return {
            visible: false,
            dataForm: {
                attrId: 0,
                attrName: '',
                searchType: 0,
                valueType: 1,
                icon: '',
                valueSelect: '',
                attrType: 1,
                enable: 1,
                catelogId: '',
                attrGroupId: '',
                showDesc: 0
            },
            attrGroupId: '',
            catelogPath: [],
            attrGroups: [],
            dataRule: {
                attrName: [
                    {
                        required: true,
                        message: '属性名不能为空',
                        trigger: 'blur'
                    }
                ],
                searchType: [
                    {
                        required: true,
                        message: '是否需要检索不能为空',
                        trigger: 'blur'
                    }
                ],
                valueType: [
                    {
                        required: true,
                        message: '值类型不能为空',
                        trigger: 'blur'
                    }
                ],
                icon: [
                    {
                        required: true,
                        message: '属性图标不能为空',
                        trigger: 'blur'
                    }
                ],
                attrType: [
                    {
                        required: true,
                        message: '属性类型不能为空',
                        trigger: 'blur'
                    }
                ],
                enable: [
                    {
                        required: true,
                        message: '启用状态不能为空',
                        trigger: 'blur'
                    }
                ],
                catelogId: [
                    {
                        required: true,
                        message: '需要选择正确的三级分类数据',
                        trigger: 'blur'
                    }
                ],
                showDesc: [
                    {
                        required: true,
                        message: '快速展示不能为空',
                        trigger: 'blur'
                    }
                ]
            }
        }
    },
    props: {
        type: {
            type: Number,
            default: 1
        }
    },

    watch: {
        catelogPath(path) {
            if (this.visible == false) return
            //监听到路径变化需要查出这个三级分类的分组信息
            this.attrGroups = []
            this.dataForm.attrGroupId = ''
            this.dataForm.catelogId = path[path.length - 1]
            if (path && path.length == 3) {
                this.$http
                    .get(`${attrGroupUrl.list}/${path[path.length - 1]}`)
                    .then(({ data }) => {
                        if (data && data.code === 200) {
                            this.attrGroups = data.data.list
                            this.dataForm.attrGroupId = this.attrGroupId
                            this.attrGroupId = ''
                        } else {
                            this.$message.error(data.msg)
                        }
                    })
            } else if (path.length == 0) {
                this.dataForm.catelogId = ''
            } else {
                this.$message.error('请选择正确的分类')
                this.dataForm.catelogId = ''
            }
        }
    },
    computed: {},
    methods: {
        init() {
            this.visible = true
            this.$nextTick(() => {
                this.$refs['dataForm'].resetFields()
                if (this.dataForm.attrId) {
                    // 编辑
                    this.getInfo()
                }
                this.initFlag = false
            })
        },
        // 获取信息
        getInfo() {
            this.$http
                .get(`/product/attr/${this.dataForm.attrId}`)
                .then(({ data: res }) => {
                    if (res.code !== 200) {
                        return this.$message.error(res.msg)
                    }
                    res.data.valueSelect = res.data.valueSelect.split(';')
                    this.dataForm = {
                        ...this.dataForm,
                        ...res.data
                    }
                    this.attrGroupId = res.data.attrGroupId
                    this.catelogPath = res.data.catelogPath
                })
                .catch(() => {})
        },
        dialogClose() {
            this.catelogPath = []
        },
        // 表单提交
        dataFormSubmitHandle: debounce(
            function() {
                this.$refs['dataForm'].validate(valid => {
                    if (!valid) {
                        return false
                    }
                    this.dataForm.valueSelect = this.dataForm.valueSelect.join(
                        ';'
                    )
                    this.$http[!this.dataForm.attrId ? 'post' : 'put'](
                        '/product/attr/',
                        this.dataForm
                    )
                        .then(({ data: res }) => {
                            if (res.code !== 200) {
                                return this.$message.error(res.msg)
                            }
                            this.$message({
                                message: this.$t('prompt.success'),
                                type: 'success',
                                duration: 500,
                                onClose: () => {
                                    this.visible = false
                                    this.$emit('refreshDataList')
                                }
                            })
                        })
                        .catch(() => {})
                })
            },
            1000,
            { leading: true, trailing: false }
        )
    },
    created() {
        this.dataForm.attrType = this.type
    }
}
</script>
