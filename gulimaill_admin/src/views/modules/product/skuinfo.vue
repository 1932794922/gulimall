<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-product__skuinfo}">
            <el-form
                :inline="true"
                :model="dataForm"
                @keyup.enter.native="getDataList()"
            >
                <el-form :inline="true" :model="dataForm">
                    <el-form-item label="分类">
                        <category-cascader
                            :catelogPath.sync="catelogPath"
                        ></category-cascader>
                    </el-form-item>
                    <el-form-item label="品牌">
                        <brand-select style="width:160px"></brand-select>
                    </el-form-item>
                    <el-form-item label="价格">
                        <el-input-number
                            style="width:160px"
                            v-model="dataForm.price.min"
                            :min="0"
                        ></el-input-number>
                        -
                        <el-input-number
                            style="width:160px"
                            v-model="dataForm.price.max"
                            :min="0"
                        ></el-input-number>
                    </el-form-item>
                    <el-form-item label="检索">
                        <el-input
                            style="width:160px"
                            v-model="dataForm.key"
                            clearable
                        ></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchSkuInfo"
                            >查询</el-button
                        >
                    </el-form-item>
                </el-form>
            </el-form>
            <el-table
                v-loading="dataListLoading"
                :data="dataList"
                border
                @selection-change="dataListSelectionChangeHandle"
                style="width: 100%;"
            >
                <el-table-column type="expand">
                    <template slot-scope="scope">
                        商品标题：{{ scope.row.skuTitle }}
                        <br />
                        商品副标题：{{ scope.row.skuSubtitle }}
                        <br />
                        商品描述：{{ scope.row.skuDesc }}
                        <br />
                        分类ID：{{ scope.row.catalogId }}
                        <br />
                        SpuID：{{ scope.row.spuId }}
                        <br />
                        品牌ID：{{ scope.row.brandId }}
                        <br />
                    </template>
                </el-table-column>
                <el-table-column
                    type="selection"
                    header-align="center"
                    align="center"
                    width="50"
                ></el-table-column>
                <el-table-column
                    prop="skuId"
                    header-align="center"
                    align="center"
                    label="skuId"
                ></el-table-column>
                <el-table-column
                    prop="skuName"
                    header-align="center"
                    align="center"
                    label="名称"
                ></el-table-column>
                <el-table-column
                    prop="skuDefaultImg"
                    header-align="center"
                    align="center"
                    label="默认图片"
                >
                    <template slot-scope="scope">
                        <img
                            :src="scope.row.skuDefaultImg"
                            style="width:80px;height:80px;"
                        />
                    </template>
                </el-table-column>
                <el-table-column
                    prop="price"
                    header-align="center"
                    align="center"
                    label="价格"
                ></el-table-column>
                <el-table-column
                    prop="saleCount"
                    header-align="center"
                    align="center"
                    label="销量"
                ></el-table-column>
                <el-table-column
                    fixed="right"
                    header-align="center"
                    align="center"
                    width="150"
                    label="操作"
                >
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            size="small"
                            @click="previewHandle(scope.row.skuId)"
                            >预览</el-button
                        >
                        <el-button
                            type="text"
                            size="small"
                            @click="commentHandle(scope.row.skuId)"
                            >评论</el-button
                        >
                        <el-dropdown
                            @command="handleCommand(scope.row, $event)"
                            size="small"
                            split-button
                            type="text"
                        >
                            更多
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="uploadImages"
                                    >上传图片</el-dropdown-item
                                >
                                <el-dropdown-item command="seckillSettings"
                                    >参与秒杀</el-dropdown-item
                                >
                                <el-dropdown-item command="reductionSettings"
                                    >满减设置</el-dropdown-item
                                >
                                <el-dropdown-item command="discountSettings"
                                    >折扣设置</el-dropdown-item
                                >
                                <el-dropdown-item command="memberPriceSettings"
                                    >会员价格</el-dropdown-item
                                >
                                <el-dropdown-item command="stockSettings"
                                    >库存管理</el-dropdown-item
                                >
                                <el-dropdown-item command="couponSettings"
                                    >优惠劵</el-dropdown-item
                                >
                            </el-dropdown-menu>
                        </el-dropdown>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                :current-page="page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="limit"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="pageSizeChangeHandle"
                @current-change="pageCurrentChangeHandle"
            >
            </el-pagination>
            <!-- 弹窗, 新增 / 修改 -->
            <add-or-update
                v-if="addOrUpdateVisible"
                ref="addOrUpdate"
                @refreshDataList="getDataList"
            ></add-or-update>
        </div>
    </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './skuinfo-add-or-update'
import CategoryCascader from '../common/CategoryCascader'
import BrandSelect from '../common/brandSelect'

export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/product/skuinfo/page',
                getDataListIsPage: true,
                exportURL: '/product/skuinfo/export',
                deleteURL: '/product/skuinfo',
                deleteIsBatch: true
            },
            dataForm: {
                skuId: '',
                key: '',
                brandId: 0,
                catelogId: 0,
                price: {
                    min: 0,
                    max: 0
                }
            },
            catelogPath: []
        }
    },
    components: {
        AddOrUpdate,
        CategoryCascader,
        BrandSelect
    },
    activated() {
        this.getDataList()
    },
    methods: {
        getSkuDetails(row, expand) {
            //sku详情查询
            console.log('展开某行...', row, expand)
        },
        //处理更多指令
        handleCommand(row, command) {
            console.log('~~~~~', row, command)
            if ('stockSettings' == command) {
                this.$router.push({
                    path: '/ware-waresku',
                    query: { skuId: row.skuId }
                })
            }
        },
        searchSkuInfo() {
            this.getDataList()
        }
    },
    mounted() {
        this.catPathSub = PubSub.subscribe('catPath', (msg, val) => {
            this.dataForm.catelogId = val[val.length - 1]
        })
        this.brandIdSub = PubSub.subscribe('brandId', (msg, val) => {
            this.dataForm.brandId = val
        })
    },
    beforeDestroy() {
        PubSub.unsubscribe(this.catPathSub)
        PubSub.unsubscribe(this.brandIdSub)
    } //生命周期 - 销毁之前
}
</script>
