$(document).ready(function () {
    $('#cert').change(function(){
        $('#newImg').html('<img class="addImagees" src="'+ window.URL.createObjectURL( $('#cert')[0].files[0])+'"  width="100%" /> ');
    })
});


Vue.config.productionTip=false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data:{
        //切换上传模式
        loading: true,   //true：上传本地图片，false：上传网络图片
        uploadName: '上传本地图片',
        //获取用户信息
        user:{
            uid: null,
            username: '',
            avatar: ''
        },
        //图片url
        formdata: {
            avatar: ''
        },
        //表单规则
        rules:{
            avatar: [{ required: true, message: 'url不能为空', trigger: 'blur' },
            ],  //blur失去焦点
        }
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created(){
        this.getUser();
    },
    methods: {
        //切换模式
        updatePattren(){
            if(this.loading){
                this.loading=false;
                this.uploadName='网络地址图片';
            }else{
                this.loading=true;
                this.uploadName='上传本地图片';
            }
        },
        //获取用户信息
        getUser(){
            axios({
                methods: "get",
                url: "/user/getUser",
            }).then((res)=>{
                if(res.data.flag){
                    this.userLoading=true;
                    this.user=res.data.data;
                }else{
                    this.userLoading=false;
                }
            });
        },
        //搜索
        searchContent(){
            location.href="search.html?context="+this.searchData;
        },
        //上传信息
        submit(){
            var type = "file";          //后台接收时需要的参数名称，自定义即可
            var id = "cert";            //即input的id，用来寻找值
            var formData = new FormData();
            formData.append(type, $("#"+id)[0].files[0]);    //生成一对表单属性
            axios({
                method: "POST",
                url: "/user/upload",
                data: formData
            }).then((res)=>{
                if(res.data.flag){
                    this.$message.success({
                        message: res.data.message,
                        type: 'success'
                    });
                    location.reload();   //刷新页面
                }else{
                    this.$message.error(res.data.message);
                }
            });
        },
        //网络地址上传
        update(){
            this.$refs.formData.validate((valid) => {
                if (valid) {
                    axios({
                        method: "PUT",
                        url: "/user/updateAvatar",
                        data: {
                            avatar: this.formdata.avatar
                        }
                    }).then((res)=>{
                        if(res.data.flag){
                            this.$message.success({
                                message: res.data.message,
                                type: 'success'
                            });
                        }else{
                            this.$message.error(res.data.message);
                        }
                    });
                    location.reload();   //刷新页面
                }else{
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            })
        }
    }
});