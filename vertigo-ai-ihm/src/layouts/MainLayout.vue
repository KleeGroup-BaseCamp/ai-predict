<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="header">
      <q-toolbar class="row inline items-center ">
        <q-img
          src="favicon.ico"
          style="height: 40px; max-width: 40px"
          class=" col logo"
        />
        <q-toolbar-title class="col title">
          AI Predict Manager
        </q-toolbar-title>

      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>