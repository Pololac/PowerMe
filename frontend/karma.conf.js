module.exports = function (config) {
  config.set({
    frameworks: ['jasmine', '@angular-devkit/build-angular'],

    browsers: ['ChromeHeadlessNoSandbox'],

    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: [
          '--headless',
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--disable-gpu',
          '--remote-debugging-port=9222',
        ],
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
