const { addCucumberPreprocessorPlugin } = require("@badeball/cypress-cucumber-preprocessor");
const { defineConfig } = require("cypress");
const webpack = require('@cypress/webpack-preprocessor')

async function setupNodeEvents(
  on,
  config
) {
  await addCucumberPreprocessorPlugin(on, config);

  on(
    "file:preprocessor",
    webpack({
      webpackOptions: {
        resolve: {
          extensions: [".ts", ".js"],
        },
        module: {
          rules: [
            {
              test: /\.ts$/,
              exclude: [/node_modules/],
              use: [
                {
                  loader: "ts-loader",
                },
              ],
            },
            {
              test: /\.feature$/,
              use: [
                {
                  loader: "@badeball/cypress-cucumber-preprocessor/webpack",
                  options: config,
                },
              ],
            },
          ],
        },
      },
    })
  );

  // Make sure to return the config object as it might have been modified by the plugin.
  return config;
}
module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:9000',
    scrollBehavior: 'center',
    specPattern: "**/cypress/**/*.feature",
    setupNodeEvents,
    retries: {
      runMode: 2,
      // openMode: 1
    }
  },
});
