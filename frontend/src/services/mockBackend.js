// src/services/mockBackend.js
//
// In-memory mock backend used when the real Spring Boot API (http://localhost:9090)
// is unreachable — e.g. inside the v0 preview where Java/PostgreSQL cannot run.
// It implements every endpoint the frontend calls and persists state to
// localStorage so created activities, comments and registrations survive reloads.
//
// When the real backend IS reachable, api.js never calls this file, so production
// behaviour is unchanged.

const STORAGE_KEY = "felles_punkt_mock_db_v1"

// ---------------------------------------------------------------------------
// Seed data
// ---------------------------------------------------------------------------

function isoInDays(days, hour = 18, minutes = 0) {
  const d = new Date()
  d.setDate(d.getDate() + days)
  d.setHours(hour, minutes, 0, 0)
  // Return a value compatible with datetime-local slicing (no timezone suffix).
  const pad = (n) => String(n).padStart(2, "0")
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:00`
}

function seed() {
  const users = [
    {
      userId: 1,
      firstName: "Astrid",
      lastName: "Admin",
      email: "admin@test.no",
      telephone: "40000001",
      role: "ADMIN",
      password: "password",
    },
    {
      userId: 2,
      firstName: "Arne",
      lastName: "Arrangør",
      email: "arrangor@test.no",
      telephone: "40000002",
      role: "ARANGOR",
      password: "password",
    },
    {
      userId: 3,
      firstName: "Dagny",
      lastName: "Deltaker",
      email: "deltaker@test.no",
      telephone: "40000003",
      role: "USER",
      password: "password",
    },
    {
      userId: 4,
      firstName: "Kari",
      lastName: "Kranheim",
      email: "kari@test.no",
      telephone: "40000004",
      role: "ARANGOR",
      password: "password",
    },
  ]

  const activities = [
    {
      activityId: 1,
      activityType: "Fotballtrening",
      holdPlace: "Voldsløkka, Oslo",
      description:
        "Uformell fotballtrening for alle nivåer. Vi deler inn i lag og spiller kamp etter oppvarming. Ta med både lyst og mørkt treningstøy.",
      startTime: isoInDays(2, 18, 0),
      endTime: isoInDays(2, 19, 30),
      image: "",
      location: { id: 101, name: "Voldsløkka" },
      organizer: { firstName: "Arne", lastName: "Arrangør", email: "arrangor@test.no" },
      registeredUsers: ["deltaker@test.no"],
    },
    {
      activityId: 2,
      activityType: "Yoga i parken",
      holdPlace: "Frognerparken, Oslo",
      description:
        "Rolig morgenyoga i det grønne. Passer for nybegynnere. Ta med egen matte og vann.",
      startTime: isoInDays(3, 9, 0),
      endTime: isoInDays(3, 10, 0),
      image: "",
      location: { id: 102, name: "Frognerparken" },
      organizer: { firstName: "Kari", lastName: "Kranheim", email: "kari@test.no" },
      registeredUsers: [],
    },
    {
      activityId: 3,
      activityType: "Klatrekurs for nybegynnere",
      holdPlace: "Oslo Klatresenter",
      description:
        "Introduksjon til innendørs buldring og topptauklatring. Utstyr er inkludert. Maks 10 deltakere.",
      startTime: isoInDays(5, 17, 0),
      endTime: isoInDays(5, 19, 0),
      image: "",
      location: { id: 103, name: "Oslo Klatresenter" },
      organizer: { firstName: "Arne", lastName: "Arrangør", email: "arrangor@test.no" },
      registeredUsers: ["deltaker@test.no", "kari@test.no"],
    },
    {
      activityId: 4,
      activityType: "Løpegruppe langs elva",
      holdPlace: "Akerselva, Oslo",
      description:
        "Felles rolig løpetur på 5 km langs Akerselva. Vi holder samlet tempo slik at ingen blir hektet av.",
      startTime: isoInDays(1, 18, 30),
      endTime: isoInDays(1, 19, 30),
      image: "",
      location: { id: 104, name: "Akerselva" },
      organizer: { firstName: "Kari", lastName: "Kranheim", email: "kari@test.no" },
      registeredUsers: [],
    },
  ]

  const comments = [
    {
      commentId: 1,
      activityId: 1,
      userEmail: "deltaker@test.no",
      text: "Gleder meg! Er banen gressbane eller kunstgress?",
      createdAt: isoInDays(-1, 12, 0),
    },
    {
      commentId: 2,
      activityId: 3,
      userEmail: "kari@test.no",
      text: "Perfekt for oss som aldri har klatret før. Anbefales!",
      createdAt: isoInDays(-2, 15, 30),
    },
  ]

  return {
    users,
    activities,
    comments,
    passwordResets: {}, // token -> email
    nextUserId: 5,
    nextActivityId: 5,
    nextCommentId: 3,
  }
}

// ---------------------------------------------------------------------------
// Persistence
// ---------------------------------------------------------------------------

let db = null

function load() {
  if (db) return db
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      db = JSON.parse(raw)
      return db
    }
  } catch {
    // fall through to fresh seed
  }
  db = seed()
  save()
  return db
}

function save() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(db))
  } catch {
    // ignore quota / availability errors
  }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function encodeToken(email) {
  return `mock.${btoa(unescape(encodeURIComponent(email)))}`
}

function decodeToken(token) {
  if (!token || !token.startsWith("mock.")) return null
  try {
    return decodeURIComponent(escape(atob(token.slice(5))))
  } catch {
    return null
  }
}

function readHeader(config, name) {
  const headers = config.headers
  if (!headers) return null
  // axios 1.x uses an AxiosHeaders instance which exposes values via get().
  if (typeof headers.get === "function") {
    return headers.get(name) || headers.get(name.toLowerCase()) || null
  }
  return headers[name] || headers[name.toLowerCase()] || null
}

function currentUser(config) {
  const auth = readHeader(config, "Authorization")
  if (!auth) return null
  const token = auth.replace(/^Bearer\s+/i, "")
  const email = decodeToken(token)
  if (!email) return null
  return load().users.find((u) => u.email.toLowerCase() === email.toLowerCase()) || null
}

function parseBody(config) {
  if (!config.data) return {}
  if (typeof config.data === "string") {
    try {
      return JSON.parse(config.data)
    } catch {
      return {}
    }
  }
  return config.data
}

function withComments(activity) {
  const activityComments = load()
    .comments.filter((c) => c.activityId === activity.activityId)
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  return { ...activity, comments: activityComments }
}

function overlaps(a, b) {
  const aStart = new Date(a.startTime).getTime()
  const aEnd = new Date(a.endTime).getTime()
  const bStart = new Date(b.startTime).getTime()
  const bEnd = new Date(b.endTime).getTime()
  return aStart < bEnd && bStart < aEnd
}

function ok(data, status = 200) {
  return {
    data,
    status,
    statusText: "OK",
    headers: { "content-type": "application/json" },
  }
}

function fail(status, message) {
  const error = new Error(message)
  error.isAxiosError = true
  error.response = {
    status,
    statusText: message,
    data: { message },
    headers: { "content-type": "application/json" },
  }
  return error
}

// ---------------------------------------------------------------------------
// Route handling
// ---------------------------------------------------------------------------

export function handleMockRequest(config) {
  const method = (config.method || "get").toLowerCase()
  // Strip baseURL / origin and query string so matching works for both relative
  // and absolute URLs (RegisterView historically used an absolute URL).
  let url = config.url || ""
  url = url.replace(/^https?:\/\/[^/]+/i, "")
  url = url.split("?")[0]

  const body = parseBody(config)
  const database = load()

  // --- Auth ---------------------------------------------------------------
  if (url === "/api/auth/login" && method === "post") {
    const identifier = (body.username || body.mailaddress || body.email || "").toLowerCase()
    const user = database.users.find((u) => u.email.toLowerCase() === identifier)
    if (!user || user.password !== body.password) {
      throw fail(401, "Feil brukernavn eller passord")
    }
    return ok({
      token: encodeToken(user.email),
      role: user.role,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
    })
  }

  if (url === "/api/auth/register" && method === "post") {
    const email = (body.email || "").toLowerCase()
    if (!email) throw fail(400, "E-post er påkrevd")
    if (database.users.some((u) => u.email.toLowerCase() === email)) {
      throw fail(409, "En bruker med denne e-posten finnes allerede")
    }
    const user = {
      userId: database.nextUserId++,
      firstName: body.firstName || "",
      lastName: body.lastName || "",
      email: body.email,
      telephone: body.telephone || "",
      role: body.role || "USER",
      password: body.password || "password",
      address: body.address || null,
    }
    database.users.push(user)
    save()
    return ok({ message: "Registrering vellykket! Du kan nå logge inn." }, 201)
  }

  if (url === "/api/auth/forgot-password" && method === "post") {
    const email = (body.email || "").toLowerCase()
    const user = database.users.find((u) => u.email.toLowerCase() === email)
    // Always respond success to avoid leaking which emails exist.
    const token = `reset-${Math.random().toString(36).slice(2, 10)}`
    if (user) {
      database.passwordResets[token] = user.email
      save()
    }
    return ok({
      message: "Hvis e-posten finnes, har vi sendt en tilbakestillingslenke.",
      token: user ? token : undefined,
    })
  }

  if (url === "/api/auth/reset-password" && method === "post") {
    const email = database.passwordResets[body.token]
    if (!email) throw fail(400, "Ugyldig eller utløpt tilbakestillingslenke")
    const user = database.users.find((u) => u.email === email)
    if (user) user.password = body.newPassword
    delete database.passwordResets[body.token]
    save()
    return ok({ message: "Passordet ditt er tilbakestilt. Du kan nå logge inn." })
  }

  // --- Activities ---------------------------------------------------------
  if (url === "/api/activities" && method === "get") {
    return ok(database.activities.map(withComments))
  }

  if (url === "/api/activities/mine" && method === "get") {
    const user = currentUser(config)
    if (!user) throw fail(401, "Ikke innlogget")
    let mine
    if (user.role === "ARANGOR") {
      mine = database.activities.filter(
        (a) => (a.organizer?.email || "").toLowerCase() === user.email.toLowerCase(),
      )
    } else {
      mine = database.activities.filter((a) =>
        (a.registeredUsers || []).some((e) => e.toLowerCase() === user.email.toLowerCase()),
      )
    }
    return ok(mine.map(withComments))
  }

  if (url === "/api/activities" && method === "post") {
    const user = currentUser(config)
    const organizer = user
      ? { firstName: user.firstName, lastName: user.lastName, email: user.email }
      : { firstName: "Ukjent", lastName: "", email: "" }
    const activity = {
      activityId: database.nextActivityId++,
      activityType: body.activityType || "",
      holdPlace: body.holdPlace || "",
      description: body.description || "",
      startTime: body.startTime || "",
      endTime: body.endTime || "",
      image: body.image || "",
      location: { id: body.locationId ?? null, name: body.holdPlace || "" },
      organizer,
      registeredUsers: [],
    }
    database.activities.push(activity)
    save()
    return ok(withComments(activity), 201)
  }

  let match = url.match(/^\/api\/activities\/(\d+)$/)
  if (match) {
    const id = Number(match[1])
    const idx = database.activities.findIndex((a) => a.activityId === id)
    if (method === "get") {
      if (idx === -1) throw fail(404, "Aktiviteten ble ikke funnet")
      return ok(withComments(database.activities[idx]))
    }
    if (method === "put") {
      if (idx === -1) throw fail(404, "Aktiviteten ble ikke funnet")
      const existing = database.activities[idx]
      database.activities[idx] = {
        ...existing,
        activityType: body.activityType ?? existing.activityType,
        holdPlace: body.holdPlace ?? existing.holdPlace,
        description: body.description ?? existing.description,
        startTime: body.startTime ?? existing.startTime,
        endTime: body.endTime ?? existing.endTime,
        image: body.image ?? existing.image,
        location: {
          id: body.locationId ?? existing.location?.id ?? null,
          name: body.holdPlace ?? existing.location?.name ?? "",
        },
      }
      save()
      return ok(withComments(database.activities[idx]))
    }
    if (method === "delete") {
      if (idx === -1) throw fail(404, "Aktiviteten ble ikke funnet")
      database.activities.splice(idx, 1)
      database.comments = database.comments.filter((c) => c.activityId !== id)
      save()
      return ok({ message: "Aktivitet slettet" })
    }
  }

  match = url.match(/^\/api\/activities\/(\d+)\/register$/)
  if (match) {
    const id = Number(match[1])
    const user = currentUser(config)
    if (!user) throw fail(401, "Ikke innlogget")
    const activity = database.activities.find((a) => a.activityId === id)
    if (!activity) throw fail(404, "Aktiviteten ble ikke funnet")

    if (method === "post") {
      const already = (activity.registeredUsers || []).some(
        (e) => e.toLowerCase() === user.email.toLowerCase(),
      )
      if (already) throw fail(409, "Du er allerede påmeldt denne aktiviteten")

      // Time-conflict check against the user's other registrations.
      const conflict = database.activities.find(
        (other) =>
          other.activityId !== id &&
          (other.registeredUsers || []).some((e) => e.toLowerCase() === user.email.toLowerCase()) &&
          overlaps(other, activity),
      )
      if (conflict) {
        throw fail(
          409,
          `Aktiviteten kolliderer med "${conflict.activityType}" som du allerede er påmeldt.`,
        )
      }

      activity.registeredUsers = [...(activity.registeredUsers || []), user.email]
      save()
      return ok({ message: "Påmelding registrert" }, 201)
    }

    if (method === "delete") {
      activity.registeredUsers = (activity.registeredUsers || []).filter(
        (e) => e.toLowerCase() !== user.email.toLowerCase(),
      )
      save()
      return ok({ message: "Avmelding registrert" })
    }
  }

  // --- Comments -----------------------------------------------------------
  match = url.match(/^\/api\/comments\/activity\/(\d+)$/)
  if (match) {
    const id = Number(match[1])
    if (method === "get") {
      const list = database.comments
        .filter((c) => c.activityId === id)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      return ok(list)
    }
    if (method === "post") {
      const user = currentUser(config)
      if (!user) throw fail(401, "Du må være innlogget for å kommentere")
      const comment = {
        commentId: database.nextCommentId++,
        activityId: id,
        userEmail: user.email,
        text: body.text || "",
        createdAt: new Date().toISOString(),
      }
      database.comments.push(comment)
      save()
      return ok(comment, 201)
    }
  }

  // --- Users (admin) ------------------------------------------------------
  if (url === "/api/users" && method === "get") {
    return ok(
      database.users.map(({ password, ...rest }) => rest), // never expose passwords
    )
  }

  match = url.match(/^\/api\/users\/(\d+)\/role$/)
  if (match && method === "put") {
    const id = Number(match[1])
    const user = database.users.find((u) => u.userId === id)
    if (!user) throw fail(404, "Bruker ble ikke funnet")
    user.role = body.role || user.role
    save()
    return ok({ message: "Rolle oppdatert", role: user.role })
  }

  // --- Fallback -----------------------------------------------------------
  throw fail(404, `Mock backend: ingen rute for ${method.toUpperCase()} ${url}`)
}

// Reset helper (handy for debugging from the console).
export function resetMockBackend() {
  db = seed()
  save()
}
