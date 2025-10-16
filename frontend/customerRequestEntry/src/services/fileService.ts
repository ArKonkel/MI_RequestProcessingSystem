import axios from 'axios'

export async function downloadFile(fileId: string): Promise<void> {
  const response = await axios.get(`/api/files/${fileId}`, {
    responseType: 'blob',
  })

  const blob = new Blob([response.data], { type: response.headers['content-type'] })

  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url

  const contentDisposition = response.headers['content-disposition']
  let fileName = 'download'
  if (contentDisposition) {
    const match = contentDisposition.match(/filename="(.+)"/) //finds filename in header
    if (match) fileName = match[1]
  }
  link.download = fileName

  //simulates click on link. Adds the link to the DOM and removes it afterward.
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}
