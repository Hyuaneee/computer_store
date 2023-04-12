/*商品列表，鼠标移入时加阴影、移出移除阴影*/
$(".goods-panel").hover(function () {
    $(this).css("box-shadow", "0px 0px 8px #888888");
}, function () {
    $(this).css("box-shadow", "");
});

//获取地址栏中的参数id
function getUrlParam(name) {
    //构造一个含有目标参数的正则表达式对象
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //匹配目标参数
    var r = window.location.search.substr(1).match(reg);
    //返回参数
    if (r != null) {
        return decodeURI(r[2]);   //decodeURI可防止乱码
    } else {
        return null;
    }
}


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
        searchData: '1123',
        //分页信息
        pageData: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 8,  //当前页条数
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
                if (res.data.code === 1) {
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
            var context = getUrlParam("context");
            this.searchData = context;
            axios({
                method: "POST",
                url: "/product/getPageList/" + this.pageData.currentPage + "/" + this.pageData.pageSize,
                params: {
                    context: context
                },
            }).then((res) => {
                console.log(res.data.data);
                this.pageData.content = res.data.value;
                this.pageData.currentPage = res.data.key.current;
                this.pageData.pageSize = res.data.key.size;
                this.pageData.total = res.data.key.total;
                this.pageData.pages = res.data.key.pages;
                this.pageData.firstPage = this.pageData.currentPage == 1 ? true : false;
                this.pageData.lastPage = this.pageData.currentPage == this.pageData.pages ? true : false;
                setTimeout(function () {
                    this.loading = false;  //关闭加载
                }, 2000)
            });
        },
        //上一页
        Previous() {
            this.pageData.currentPage = this.pageData.currentPage - 1;
            this.findPage();
        },
        //下一页
        Next() {
            this.pageData.currentPage = this.pageData.currentPage + 1;
            this.findPage();
        },
        //加入收藏
        insertCollect(id) {
            axios({
                method: "get",
                url: "/collect/insertCollect/" + id,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.$message.success({
                        message: res.data.message,
                        type: 'success'
                    });
                    $('#collectA' + id).html("<span class='fa fa-heart-o'></span>已加入收藏");
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
        //加入购物车
        insertCart(id) {
            axios({
                method: "get",
                url: "/cart/insert",
                params: {
                    pid: id
                }
            }).then((res) => {
                if (res.data.code === 1) {
                    this.$message.success({
                        message: res.data.message,
                        type: 'success'
                    });
                    $('#collectB' + id).html("<span class=\"glyphicon glyphicon-ok\"></span>已加入购物车");
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
        //跳转到商品展示页
        productShow(pid) {
            location.href = "product.html?id=" + pid;
        }

    }
});