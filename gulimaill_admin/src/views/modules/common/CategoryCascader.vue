<template>
    <!--
    使用说明：
    1）、引入category-cascader.vue
    2）、语法：<category-cascader :catelogPath.sync="catelogPath"></category-cascader>
        解释：
          catelogPath：指定的值是cascader初始化需要显示的值，应该和父组件的catelogPath绑定;
              由于有sync修饰符，所以cascader路径变化以后自动会修改父的catelogPath，这是结合子组件this.$emit("update:catelogPath",v);做的
          -->
    <div>
        <el-cascader
            filterable
            clearable
            placeholder="试试搜索：手机"
            v-model="paths"
            :options="categorys"
            :props="setting"
        ></el-cascader>
    </div>
</template>

<script>
import { categoryUrl } from '@/api/product/index'
export default {
    //import引入的组件需要注入到对象中才能使用
    components: {},
    //接受父组件传来的值
    props: {
        catelogPath: {
            type: Array,
            default() {
                return []
            }
        }
    },
    data() {
        //这里存放数据
        return {
            setting: {
                value: 'catId',
                label: 'name',
                children: 'children'
            },
            categorys: [],
            paths: this.catelogPath
        }
    },
    watch: {
        catelogPath(v) {
            this.paths = this.catelogPath
        },
        paths(v) {
            if (v?.length === 0) {
                return
            }
            console.log(v)
            this.$emit('update:catelogPath', v)
            //还可以使用pubsub-js进行传值
            PubSub.publish('catPath', v)
        }
    },
    //方法集合
    methods: {
        getCategorys() {
            this.$http
                .get(categoryUrl.getCategoryTreeList)
                .then(({ data: res }) => {
                    if (res.code !== 200) {
                        return this.$message.error(res.msg)
                    }
                    this.categorys = res.data
                })
                .catch(() => {})
        }
    },
    //生命周期 - 创建完成（可以访问当前this实例）
    created() {
        this.getCategorys()
    }
}
</script>
<style scoped></style>
