export interface SideNavItem {
  label: string
  icon: string
  route: string
  description?: string
  badge?: string
}

export interface SideNavSection {
  label: string
  items: SideNavItem[]
}

export interface SideNavAction {
  label: string
  description: string
  icon: string
  route: string
}

export interface SideNavProfile {
  name: string
  role: string
  email: string
  status?: string
}
