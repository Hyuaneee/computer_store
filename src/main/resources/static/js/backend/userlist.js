Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->
const v = new Vue({
    el: '#app',
    data: {
        loading: false,   //页面加载,false:关闭,true:加载中
        //用户信息
        formData: {},

        //分页数据
        page: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 4,  //当前页条数
            total: 1,  //数据总条数
            pages: 1,  //总页数
            firstPage: false,  //是否禁用前一页按钮
            lastPage: false,  //是否有禁用下一页按钮
        }
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.findPage();
    },
    methods: {
        //获取当前页数据
        findPage() {
            this.loading = true;
            axios({
                method: "POST",
                url: "/user/PageList/" + this.page.currentPage + "/" + this.page.pageSize,
                data: {},
            }).then((res) => {
                this.page.content = res.data.data.records;
                this.page.currentPage = res.data.data.current;
                this.page.pageSize = res.data.data.size;
                this.page.total = res.data.data.total;
                this.page.pages = res.data.data.pages;
                this.page.firstPage = this.page.currentPage == 1 ? true : false;
                this.page.lastPage = this.page.currentPage == this.page.pages ? true : false;
                setTimeout(function () {
                }, 1000)
                this.loading = false;  //关闭加载
            });
        },
        //启用禁用
        setStatus(row) {
            this.$confirm("此操作将从更改帐号状态，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/user/setStatus/" + row.uid + "/" + row.status,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "账号状态修改成功",
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.message);
                    }
                }).finally(() => {
                    this.findPage();
                });
            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消修改"
                });
            });
        },
        //启用禁用
        setDeleted(row) {
            this.$confirm("此操作将从删除用户，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/user/setDeleted/" + row.uid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "账号已删除！",
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.message);
                    }
                }).finally(() => {
                    this.findPage();
                });
            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消修改"
                });
            });
        },
        //上一页
        Previous() {
            this.page.currentPage = this.page.currentPage - 1;
            this.findPage();
        },
        //下一页
        Next() {
            this.page.currentPage = this.page.currentPage + 1;
            this.findPage();
        }
    }
});