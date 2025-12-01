package com.example.library

import io.github.jan.supabase.*

val supabase = SupabaseClient(
    supabaseUrl = "https://iuzlnadvrkhmcmhedejt.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1emxuYWR2cmtobWNtaGVkZWp0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1MjIyNTUsImV4cCI6MjA4MDA5ODI1NX0.38oazH73ZQUoVqWLjPyrEwaAeU2tpsZe_9VKilfTMxw\""
)

val postgrest: Postgrest = supabase.postgrest
val auth: Auth = supabase.auth
val storage: Storage = supabase.storage

