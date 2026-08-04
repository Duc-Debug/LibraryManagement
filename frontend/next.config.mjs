/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
  devIndicators: false,
  allowedDevOrigins: ['172.31.208.1', 'localhost:3000', 'localhost', '127.0.0.1'],
}

export default nextConfig

