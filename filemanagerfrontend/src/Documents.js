import { Table, Button, Space, Modal, Popconfirm ,message} from "antd";
import { Component } from "react";
import 'antd/dist/antd.css';
import EditModal from "./modals/editModal"
import AddFileModal from './modals/addFileModal'
import Search from "antd/lib/input/Search";

export default class Documents extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tempTableData:[],
            tableData: [],
            editFlag: false,
            fileName: "",
            currentFile: '',
            fileExtension: '',
            addModal:false,
            newFile:null
      
        }

    }

    componentDidMount() {
       this.getFiles()
    }
    getFiles=()=>{
        fetch("http://localhost:8080/file/viewAllFiles")
        .then(res => res.json()).then((response => {
            this.setState({
                tableData: response,tempTableData:response
            })
        }))
    }
    setEditModal = (fileName) => {
        this.setState({
            editFlag: true,
            currentFile: fileName.split(".")[0],
            fileExtension: fileName.split(".")[1],
            fileName: fileName,
        })
    }
    setCreateModal =()=>{
        this.setState({
            addModal:true
        })
    }
    handleCancel = () => {
        this.setState({
            editFlag: false,
            currentFile: '',
            fileName: '', fileExtension: '',addModal:false
        })
    }
    changeName = (e) => {
        let fileName = e.target.value
        this.setState({
            currentFile: fileName
        })
    }

    renameFile = () => {
        let fileName = this.state.currentFile;
        let previousName = this.state.fileName;
        let fileExtension = this.state.fileExtension;
        let body = {
            "updateFileName": fileName + "." + fileExtension
        }
        fetch("http://localhost:8080/file/updateFile/" + previousName, {
            method: "PUT",
            body: JSON.stringify(body),
            headers: {"Content-Type": "application/json"}
        }).then(res => {
            if(res.status === 200){
                
                message.success("Successfully Updated file name!")
                this.setState({
                    editFlag:false
                },()=>this.getFiles())
                
            }
            else{
                message.error("Something went wrong")
            }
        })
    }
    cancel = (e) => {
        console.log(e);
    }

    deleteconfirm = (e,fileName) => {
        fetch("http://localhost:8080/file/remove/" + fileName,
            {
                method: "DELETE",
            }).
            then(res => {
                if(res.status === 200){
                    message.success("File deleted Successfully!")
                    this.getFiles()
                }
                else{
                    message.error("Something went wrong")
                }
            
            })
           

    }
onFileChange=(e)=>{
this.setState({
    newFile :e.target.files[0]
})
}
    addFile=()=>{
let file = this.state.newFile

let formData= new FormData();
formData.append('file',file)

fetch("http://localhost:8080/file/upload",{
    method:"POST",
    body:formData
}).then(res=>{
    if(res.status === 201)
    {
        message.success("File Added Successfully!")
        this.setState({
            addModal:false
        },()=>{
            this.getFiles()
        })
    }
    else{
        message.error("Something went wrong")
    }
})
    }
    
    viewFile=(fileName)=>{
fetch("http://localhost:8080/file/view/"+fileName).then(res=>window.open(res.url))
    }

    onSearch=(query)=>{
        let data= this.state.tempTableData;
        let searchData=data.filter(ele=>
            ele.Name.toLowerCase().includes(query.toLowerCase()) ?
                 ele
                : null  
        )
        this.setState({
            tableData:searchData
        })
    }

    render() {
        const { editFlag, tableData, currentFile,addModal,newFile } = this.state
        const columns = [
            {
                title: 'File Name',
                dataIndex: 'Name',
                key: 'Name',
                render:(text,record)=>(
                    <span style={{cursor:'pointer'}} onClick={()=>this.viewFile(text)}>{text}</span>
                )
            },
            {
                title: 'Action',
                key: 'action',
                render: (text, record) => (
                    <Space size="middle">
                        <Popconfirm
                            title="Are you sure to delete this file?"
                            onConfirm={(e)=>this.deleteconfirm(e,record.Name)}
                            onCancel={this.cancel}
                            okText="Yes"
                            cancelText="No"
                        >
                            <Button>Delete</Button>
                        </Popconfirm>
                        <Button onClick={() => this.setEditModal(record.Name)}>Edit</Button>
                    </Space>
                ),
            }
        ]
        return (
            <div style={{marginTop:'5px'}}>
                <Button type="primary" style={{float :'right',marginRight:'60px'}} onClick={this.setCreateModal}>Add file</Button>
                <Search style={{float:'right',marginRight:'40px',width:'400px'}} onChange={(e)=>this.onSearch(e.target.value)}/>
                <Table columns={columns} dataSource={tableData} pagination={false} />
                {editFlag &&
                    <Modal title="Rename File" visible={editFlag} onOk={this.renameFile} onCancel={this.handleCancel}>
                        <EditModal fileName={currentFile} changeName={this.changeName} />
                    </Modal>
                }
                {addModal &&
                    <Modal title="Add File" visible={addModal} onOk={this.addFile} onCancel={this.handleCancel}>
                        <AddFileModal fileName={newFile} onFileChange={this.onFileChange} />
                    </Modal>
                }
            </div>
        )
    }
}