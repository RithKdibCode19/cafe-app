/**
 * Composable for managing the current branch selection
 * Used for multi-branch POS setups
 */
export const useBranch = () => {
  const currentBranch = useState<Branch | null>('currentBranch', () => null)
  const branches = useState<Branch[]>('branches', () => [])
  const { get } = useApi()

  interface Branch {
    branchId: number
    code: string
    name: string
    location?: string
  }

  // Load branch from localStorage on init
  const initBranch = () => {
    if (import.meta.client) {
      const stored = localStorage.getItem('selectedBranch')
      if (stored) {
        try {
          currentBranch.value = JSON.parse(stored)
        } catch (e) {
          localStorage.removeItem('selectedBranch')
        }
      }
    }
  }

  // Fetch all branches
  const fetchBranches = async () => {
    try {
      const data = await get<Branch[]>('/branches')
      branches.value = data || []
      return data || []
    } catch (e) {
      console.error('Failed to fetch branches', e)
      return []
    }
  }

  // Set the current branch
  const setBranch = (branch: Branch) => {
    currentBranch.value = branch
    if (import.meta.client) {
      localStorage.setItem('selectedBranch', JSON.stringify(branch))
    }
  }

  // Clear branch selection
  const clearBranch = () => {
    currentBranch.value = null
    if (import.meta.client) {
      localStorage.removeItem('selectedBranch')
    }
  }

  // Check if user needs to select a branch
  const needsBranchSelection = computed(() => {
    return branches.value.length > 1 && !currentBranch.value
  })

  return {
    currentBranch: readonly(currentBranch),
    branches: readonly(branches),
    needsBranchSelection,
    initBranch,
    fetchBranches,
    setBranch,
    clearBranch
  }
}
