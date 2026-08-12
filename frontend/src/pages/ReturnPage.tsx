import { ReturnsPage } from "@/features/returns";

interface ReturnPageProps {
  records?: unknown;
  setRecords?: unknown;
  books?: unknown;
  setBooks?: unknown;
}

export default function ReturnPage(_props: ReturnPageProps = {}) {
  return <ReturnsPage />;
}
