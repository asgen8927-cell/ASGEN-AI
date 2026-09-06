import { getApp, getApps, initializeApp } from 'firebase/app'
import { getAuth } from 'firebase/auth'

const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY ?? process.env.apiKey ?? '',
  authDomain: 'asgen-ai.firebaseapp.com',
  databaseURL: 'https://asgen-ai-default-rtdb.firebaseio.com',
  projectId: 'asgen-ai',
  storageBucket: 'asgen-ai.firebasestorage.app',
  messagingSenderId: '522692679382',
  appId: '1:522692679382:web:40dae60cb95025b84d1b77',
  measurementId: 'G-1B58E8B3V1',
}

const hasApiKey = Boolean(firebaseConfig.apiKey && firebaseConfig.apiKey !== 'process.env.apiKey')

export const firebaseApp = hasApiKey ? (getApps().length ? getApp() : initializeApp(firebaseConfig)) : null
export const firebaseAuth = firebaseApp ? getAuth(firebaseApp) : null
