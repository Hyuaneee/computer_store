Vue.config.productionTip = false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        userLoading: false,  //用户是否显示
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //搜索
        searchData: null,
        //热销排行
        page: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 6,  //当前页条数
        },
        //推荐栏目
        pageRecommend: {
            content: [],
            currentPage: 2,   //当前页
            pageSize: 6,  //当前页条数
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
        //获取热销排行前五,推荐栏目前五
        findPage() {
            axios({
                method: "POST",
                url: "/product/getBestList/" + this.page.currentPage + "/" + this.page.pageSize,
                data: {},
            }).then((res) => {
                this.page.content = res.data.data.records;
            });
            axios({
                method: "POST",
                url: "/product/getBestList/" + this.pageRecommend.currentPage + "/" + this.pageRecommend.pageSize,
                data: {},
            }).then((res) => {
                this.pageRecommend.content = res.data.data.records;
            });
        },
    }
});