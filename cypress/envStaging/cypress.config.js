const { addCucumberPreprocessorPlugin } = require("@badeball/cypress-cucumber-preprocessor");
const { defineConfig } = require("cypress");
const webpack = require('@cypress/webpack-preprocessor')

// Run Command 
// npx cypress open --config-file cypress/envStaging/cypress.config.js
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
    baseUrl: 'https://csse-s302g3.canterbury.ac.nz/test/portfolio/',
    scrollBehavior: 'center',
    specPattern: "**/cypress/**/*.feature",
    setupNodeEvents,
  },
});
