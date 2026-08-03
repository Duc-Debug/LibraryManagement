// PUBLIC API: chỉ export những gì được phép dùng bên ngoài feature "dashboard"

export { Dashboard } from './components/Dashboard';

export { getActivityLogs, mockActivityLogs } from './api/getActivityLogs';

export type { ActivityLog } from './types/dashboard.types';
