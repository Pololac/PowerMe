module.exports = function (config) {
  config.set({
    frameworks: ['jasmine', '@angular-devkit/build-angular'],

    browsers: ['ChromeHeadlessNoSandbox'],

    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox', '--disable-gpu', '--disable-dev-shm-usage'],
      },
    },

    reporters: ['progress', 'kjhtml', 'junit'],

    junitReporter: {
      outputDir: 'test-results',
      outputFile: 'junit.xml',
      useBrowserName: false,
    },

    singleRun: true,
  });
};
