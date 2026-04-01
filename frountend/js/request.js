// 设置统一的后端地址（现在可以先空着，或者写个本地测试地址）
const instance = axios.create({
    baseURL: 'http://localhost:3000/api', 
    timeout: 5000
});

// 添加请求拦截器：自动给每个请求带上 Token
instance.interceptors.request.use(config => {
    const token = localStorage.getItem('userToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 添加响应拦截器：统一处理报错
instance.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response.status === 401) {
            alert("登录过期，请重新登录");
            window.location.href = 'logIn.html';
        }
        return Promise.reject(error);
    }
);