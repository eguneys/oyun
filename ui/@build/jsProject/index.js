// const gulp = require('gulp');
const source = require('vinyl-source-stream');
const buffer = require('vinyl-buffer');
const watchify = require('watchify');
const browserify = require('browserify');
const terser = require('gulp-terser');


module.exports = (gulp, standalone, fileBaseName, dir) => {
  
  const browserifyOpts = (debug) => ({
    entries: [`${dir}/src/main.js`],
    standalone,
    debug
  });
  const destination = () => gulp.dest('../../public/compiled/');

  const dev = () => browserify(browserifyOpts(true))
        .transform('babelify',
                   { presets: ['@babel/preset-env'] })
        .bundle()
        .pipe(source(`${fileBaseName}.js`))
        .pipe(destination());

  const watch = () => {
    const bundle = () => bundler
          .bundle()
          .on('error', error => console.log(error.message))
          .pipe(source(`${fileBaseName}.js`))
          .pipe(destination());

    const bundler = watchify(
      browserify(Object.assign({}, watchify.args, browserifyOpts(true)))
        .transform('babelify',
                   { presets: ['@babel/preset-env'] })
    ).on('update', bundle);

    return bundle();
  };
  

  gulp.task('dev', dev);
  gulp.task('default', watch);
};
