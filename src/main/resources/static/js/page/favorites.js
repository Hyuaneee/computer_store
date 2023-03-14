$(function () {
    /*商品列表，鼠标移入时加阴影、移出移除阴影*/
    $(".goods-panel").hover(function () {
        $(this).css("box-shadow", "0px 0px 8px #888888");
    }, function () {
        $(this).css("box-shadow", "");
    })
});

Vue.config.productionTip = false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        userLoading: false,  //用户是否显示
        loading: false, //信息是否显示
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //搜索
        searchData: null,
        //分页信息
        pageData: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 12,  //当前页条数
            total: 1,  //数据总条数
            pages: 1,  //总页数
            firstPage: false,  //是否禁用前一页按钮
            lastPage: false,  //是否有禁用下一页按钮
        }

    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.findPage();
    },
    methods: {
        //获取用户信息
        getUser() {
            axios({
                method: "get",
                url: "/user/getUser",
            }).then((res) => {
                if (res.data.code == 1) {
                    this.userLoading = true;
                    this.user = res.data.data;
                } else {
                    this.userLoading = false;
                }
            });
        },
        //搜索
        searchContent() {
            location.href = "search.html?context=" + this.searchData;
        },
        //获取当前页数据
        findPage() {
            this.loading = true;
            axios({
                method: "POST",
                url: "/collect/getPageList/" + this.pageData.currentPage + "/" + this.pageData.pageSize,
                data: {},
            }).then((res) => {
                console.log(res.data);
                this.pageData.content = res.data.value;
                this.pageData.currentPage = res.data.key.current;
                this.pageData.pageSize = res.data.key.size;
                this.pageData.total = res.data.key.total;
                this.pageData.pages = res.data.key.pages;
                this.pageData.firstPage = this.pageData.currentPage == 1 ? true : false;
                this.pageData.lastPage = this.pageData.currentPage == this.pageData.pages ? true : false;
                setTimeout(function () {
                }, 1000)
                this.loading = false;  //关闭加载
            });
        },

        //取消收藏
        deleteCollect(id) {
            this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/collect/delete/" + id,
                }).then((res) => {
                    if (res.data.code == 1) {
                        this.$message.success({
                            message: "已取消收藏！",
                            type: 'success'
                        });
                        location.reload();
                    } else {
                        this.$message.error(res.data.message);
                    }
                });
            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消修改"
                });
            });
        },
        //跳转到商品展示页
        productShow(pid) {
            location.href = "product.html?id=" + pid;
        }

    }
});