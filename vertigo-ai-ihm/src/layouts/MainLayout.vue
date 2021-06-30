<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="header">
      <q-toolbar class="row inline items-center ">
        <q-img
          src="favicon.ico"
          style="height: 40px; max-width: 40px"
          class=" col logo"
        />
        <q-toolbar-title class="col title ">
          AI Predict Manager
        </q-toolbar-title>
        <q-btn-dropdown flat dropdown-icon="add">
          <q-list>
            <q-item clickable v-close-popup to="/create">
              <q-item-section>
                <q-item-label>Create bundle</q-item-label>
              </q-item-section>
            </q-item>

            <q-item clickable v-close-popup to="/deploy">
              <q-item-section>
                <q-item-label>Deploy model</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-btn-dropdown>

        <q-btn flat color="white" to="/">
          <q-icon
            name="home"
            class="text"
            style="font-size: 1.5em; max-width: 5px;"
          />
          <q-tooltip>
            Home
          </q-tooltip>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>
