var path = require('path');
var webpack = require('webpack');

const VueLoaderPlugin = require('vue-loader/lib/plugin');

module.exports = {
    mode: 'production',
    entry: './src/main/Vue/controller.js',
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static'),
        publicPath: '/',
        filename: 'controller.js'
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [ 'vue-style-loader', 'css-loader' ]
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    plugins: [
        new VueLoaderPlugin()
    ]
};

if (process.env.NODE_ENV === 'production') {
    module.exports.plugins = (module.exports.plugins || []).concat([
        new webpack.optimize.UglifyJsPlugin({})])}